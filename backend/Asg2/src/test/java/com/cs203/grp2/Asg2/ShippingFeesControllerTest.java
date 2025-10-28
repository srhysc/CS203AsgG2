package com.cs203.grp2.Asg2;

import com.cs203.grp2.Asg2.controller.ShippingFeesController; // import controller
import com.cs203.grp2.Asg2.models.ShippingFees;
import com.cs203.grp2.Asg2.service.ShippingFeesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ShippingFeesControllerTest {

    @Mock
    private ShippingFeesService svc;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        ShippingFeesController controller = new ShippingFeesController(svc);
        this.mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getFee_found_returns200WithJson() throws Exception {

        ShippingFees fee = new ShippingFees();
        fee.setImportingCountry("SG");
        fee.setExportingCountry("CN");
        fee.setFee(123.45);

        when(svc.getFee("SG", "CN")).thenReturn(fee);

        MockHttpServletRequestBuilder req = get("/shippingFees")
                .param("importing", "SG")
                .param("exporting", "CN")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.importingCountry").value("SG"))
                .andExpect(jsonPath("$.exportingCountry").value("CN"))
                .andExpect(jsonPath("$.fee").value(123.45));

        verify(svc).getFee("SG", "CN");
        verifyNoMoreInteractions(svc);
    }

    @Test
    void getFee_notFound_returns404() throws Exception {
        when(svc.getFee("SG", "CN")).thenReturn(null);

        MockHttpServletRequestBuilder req = get("/shippingFees")
                .param("importing", "SG")
                .param("exporting", "CN");

        mvc.perform(req).andExpect(status().isNotFound());
        verify(svc).getFee("SG", "CN");
        verifyNoMoreInteractions(svc);
    }

    @Test
    void addShippingFee_validBody_returns200() throws Exception {

        String body = "{ \"importingCountry\": \"SG\", \"exportingCountry\": \"CN\", \"fee\": 100.0 }";

        MockHttpServletRequestBuilder req = post("/shippingFees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(req).andExpect(status().isOk());
        verify(svc).addShippingFee(any(ShippingFees.class));
        verifyNoMoreInteractions(svc);
    }
}
