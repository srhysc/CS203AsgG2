package com.cs203.grp2.Asg2;

import com.cs203.grp2.Asg2.controller.TradeAgreementController;
import com.cs203.grp2.Asg2.models.TradeAgreement;
import com.cs203.grp2.Asg2.service.TradeAgreementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class TradeAgreementControllerTest {

    @Mock
    private TradeAgreementService svc;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        this.mvc = MockMvcBuilders.standaloneSetup(new TradeAgreementController(svc)).build();
    }

    @Test
    void getByName_found_returns200() throws Exception {
        when(svc.getByAgreementName("A"))
                .thenReturn(Optional.of(new TradeAgreement("A", "SG", "MY")));

        mvc.perform(get("/tradeAgreements/A").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(svc).getByAgreementName("A");
        verifyNoMoreInteractions(svc);
    }

    @Test
    void getByName_notFound_returns404() throws Exception {
        when(svc.getByAgreementName("X")).thenReturn(Optional.empty());

        mvc.perform(get("/tradeAgreements/X"))
                .andExpect(status().isNotFound());

        verify(svc).getByAgreementName("X");
        verifyNoMoreInteractions(svc);
    }

    @Test
    void create_valid_returns200_andCallsAdd() throws Exception {
        String body = "{ \"agreementName\":\"A\",\"countryA\":\"SG\",\"countryB\":\"MY\" }";

        MockHttpServletRequestBuilder req = post("/tradeAgreements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(req).andExpect(status().isOk());
        verify(svc).addTradeAgreement(any(TradeAgreement.class));
        verifyNoMoreInteractions(svc);
    }

    @Test
    void update_existing_returns200() throws Exception {
        when(svc.updateAgreement(eq("A"), any(TradeAgreement.class))).thenReturn(true);

        String body = "{ \"agreementName\":\"A\",\"countryA\":\"SG\",\"countryB\":\"MY\" }";

        MockHttpServletRequestBuilder req = put("/tradeAgreements/A")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(req).andExpect(status().isOk());
        verify(svc).updateAgreement(eq("A"), any(TradeAgreement.class));
        verifyNoMoreInteractions(svc);
    }

    @Test
    void update_notFound_returns404() throws Exception {
        when(svc.updateAgreement(eq("A"), any(TradeAgreement.class))).thenReturn(false);

        String body = "{ \"agreementName\":\"A\",\"countryA\":\"SG\",\"countryB\":\"MY\" }";

        MockHttpServletRequestBuilder req = put("/tradeAgreements/A")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        mvc.perform(req).andExpect(status().isNotFound());
        verify(svc).updateAgreement(eq("A"), any(TradeAgreement.class));
        verifyNoMoreInteractions(svc);
    }

    @Test
    void delete_existing_returns200() throws Exception {
        when(svc.deleteAgreement("A")).thenReturn(true);

        mvc.perform(delete("/tradeAgreements/A"))
                .andExpect(status().isOk());

        verify(svc).deleteAgreement("A");
        verifyNoMoreInteractions(svc);
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        when(svc.deleteAgreement("X")).thenReturn(false);

        mvc.perform(delete("/tradeAgreements/X"))
                .andExpect(status().isNotFound());

        verify(svc).deleteAgreement("X");
        verifyNoMoreInteractions(svc);
    }
}
