package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.models.TariffLatest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WitsTariffService {
  private final WebClient wits;
  private final ObjectMapper om = new ObjectMapper();

  public WitsTariffService(WebClient witsWebClient) { this.wits = witsWebClient; }

  public TariffLatest getLatest(int reporterIso3n, int partnerIso3n, String hs6Product, String preferredDatatype) {
    return fetchLatest(reporterIso3n, partnerIso3n, hs6Product, preferredDatatype)
        .orElseGet(() -> fetchLatest(reporterIso3n, partnerIso3n, hs6Product,
            preferredDatatype.equalsIgnoreCase("aveestimated") ? "reported" : "aveestimated")
            .orElseThrow(() -> new IllegalStateException("No tariff found")));
  }

  private Optional<TariffLatest> fetchLatest(int reporter, int partner, String product, String datatype) {
    String url = String.format(
      "/API/V1/SDMX/V21/datasource/TRN/reporter/%d/partner/%d/product/%s/year/all/datatype/%s?format=JSON",
      reporter, partner, URLEncoder.encode(product, StandardCharsets.UTF_8), datatype.toLowerCase());

    String body = wits.get().uri(url).retrieve().bodyToMono(String.class)
        .timeout(Duration.ofSeconds(45))
        .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)))
        .block();

    try {
      JsonNode root = om.readTree(body);
      List<Integer> years = new ArrayList<>();
      JsonNode yearVals = root.path("structure").path("dimensions").path("observation").get(0).path("values");
      yearVals.forEach(v -> {
        String id = v.path("id").asText("");
        if (id.matches("\\d{4}")) years.add(Integer.parseInt(id));
      });
      if (years.isEmpty()) return Optional.empty();

      JsonNode series = root.path("dataSets").get(0).path("series");
      if (!series.fieldNames().hasNext()) return Optional.empty();
      String firstKey = series.fieldNames().next();
      JsonNode obs = series.path(firstKey).path("observations");

      for (int idx = years.size() - 1; idx >= 0; idx--) {
        JsonNode arr = obs.path(String.valueOf(idx));
        if (arr.isArray() && arr.size() > 0 && !arr.get(0).isNull()) {
          double v = arr.get(0).asDouble();
          return Optional.of(new TariffLatest(
              years.get(idx), v, String.valueOf(reporter), String.valueOf(partner),
              product, datatype.toLowerCase()));
        }
      }
      return Optional.empty();
    } catch (Exception e) {
      throw new RuntimeException("Parse failed for WITS response", e);
    }
  }
}
