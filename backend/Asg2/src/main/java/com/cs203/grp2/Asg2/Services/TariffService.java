package CS203AsgG2/backend/Asg2/src/main/java/com/cs203/grp2/Asg2/Services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TariffService {

    private final RestTemplate restTemplate;

    @Autowired
    public TariffService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getTariffRate(String importCountry, String exportCountry, String commodity) {
        // Example using WITS API
        String url = "https://wits.worldbank.org/API_URL?importCountry=" + importCountry + "&exportCountry=" + exportCountry + "&commodity=" + commodity;
        try {
            TariffResponseWrapper response = restTemplate.getForObject(url, TariffResponseWrapper.class);
            if (response != null) {
                return response.getTariffRate();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch tariff: " + e.getMessage());
        }
        return 0.0;
    }

    // Example wrapper for API response
    public static class TariffResponseWrapper {
        private double tariffRate;
        public double getTariffRate() { return tariffRate; }
        public void setTariffRate(double tariffRate) { this.tariffRate = tariffRate; }
    }
}
