package CS203AsgG2.backend.Asg2.src.main.java.com.cs203.grp2.Asg2.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sg.edu.smu.cs203.g12.cs203g12tariff.models.VatResponse;

@Service
public class VATService {

    private final RestTemplate restTemplate = new RestTemplate();

    public double getVatRate(String country) {
        String url = "https://api.example.com/vat?country=" + country;
        try {
            VatResponse response = restTemplate.getForObject(url, VatResponse.class);
            if (response != null) return response.getVatRate();
        } catch (Exception e) {
            System.err.println("Failed to fetch VAT: " + e.getMessage());
        }
        return 0.07; // fallback default 7%
    }

    public double calculateVat(double basePrice, double shippingFee, double discount, double vatRate) {
        double taxableAmount = basePrice + shippingFee - discount;
        return taxableAmount * vatRate;
    }
}
