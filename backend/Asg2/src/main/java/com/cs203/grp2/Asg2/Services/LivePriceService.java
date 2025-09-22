package CS203AsgG2.backend.Asg2.src.main.java.com.cs203.grp2.Asg2.Services;

import package CS203AsgG2.backend.Asg2.src.main.java.com.cs203.grp2.Asg2.Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LivePriceService {

    private final RestTemplate restTemplate;

    @Autowired
    public LivePriceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getCurrentPrice(String petroleumType, String currency) {
        // Example using Commodities API
        String url = "https://commodities-api.com/api/latest?access_key=YOUR_KEY&base=USD&symbols=" + currency + "&commodity=" + petroleumType;

        PriceResponse response = restTemplate.getForObject(url, PriceResponse.class);
        return response != null ? response.getPrice() : 0.0;
    }
}
