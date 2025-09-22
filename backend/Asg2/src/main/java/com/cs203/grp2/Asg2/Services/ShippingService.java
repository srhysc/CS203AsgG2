package CS203AsgG2.backend.Asg2.src.main.java.com.cs203.grp2.Asg2.Services;
  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ShippingService {

    private final RestTemplate restTemplate;

    @Autowired
    public ShippingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getShippingFee(String country, double quantity) {
        // Example API URL, replace with real one
        String url = "https://api.example.com/shipping?country=" + country + "&quantity=" + quantity;
        try {
            ShippingResponse response = restTemplate.getForObject(url, ShippingResponse.class);
            if (response != null) return response.getFee();
        } catch (Exception e) {
            System.err.println("Failed to fetch shipping fee: " + e.getMessage());
        }
        return 10.0 * quantity; // default flat rate
    }

    public static class ShippingResponse {
        private double fee;
        public double getFee() { return fee; }
        public void setFee(double fee) { this.fee = fee; }
    }
}
