package CS203AsgG2.backend.Asg2.src.main.java.com.cs203.grp2.Asg2.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate;

    @Autowired
    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double convert(double amount, String from, String to) {
        String url = "https://api.exchangeratesapi.io/latest?base=" + from + "&symbols=" + to;
        try {
            ExchangeResponse response = restTemplate.getForObject(url, ExchangeResponse.class);
            if (response != null && response.getRates() != null && response.getRates().containsKey(to)) {
                return amount * response.getRates().get(to);
            }
        } catch (RestClientException e) {
            System.err.println("Currency conversion failed: " + e.getMessage());
        }
        return amount; // fallback
    }

    public static class ExchangeResponse {
        private Map<String, Double> rates;
        public Map<String, Double> getRates() { return rates; }
        public void setRates(Map<String, Double> rates) { this.rates = rates; }
    }
}
