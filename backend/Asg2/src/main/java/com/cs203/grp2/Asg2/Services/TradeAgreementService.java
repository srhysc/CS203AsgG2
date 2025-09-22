package CS203AsgG2.backend.Asg2.src.main.java.com.cs203.grp2.Asg2.Services;

import sg.edu.smu.cs203.g12.cs203g12tariff.models.TradeAgreement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class TradeAgreementService {

    private final RestTemplate restTemplate;

    @Autowired
    public TradeAgreementService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<TradeAgreement> getTradeAgreements(String countryA, String countryB) {
        // Replace with real API URL when available
        String url = "https://api.example.com/trade-agreements?from=" + countryA + "&to=" + countryB;

        try {
            TradeAgreement[] agreements = restTemplate.getForObject(url, TradeAgreement[].class);
            if (agreements != null) {
                return Arrays.asList(agreements);
            }
        } catch (RestClientException e) {
            System.err.println("Failed to fetch trade agreements: " + e.getMessage());
        }

        return Collections.emptyList();
    }
}
