package com.cs203.grp2.Asg2;

import com.cs203.grp2.Asg2.controller.TariffController;
import com.cs203.grp2.Asg2.models.WitsTariff;
import com.cs203.grp2.Asg2.service.WitsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TariffControllerTest {

    @Mock
    private WitsService witsService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new TariffController(witsService)).build();
    }

    @Test
    void getTariff_happyPath_returnsJson() throws Exception {
        WitsTariff res = new WitsTariff("BGR", "CHN", "271012",
                LocalDate.parse("2025-10-14"), 5.0, "mfn", "test");
        when(witsService.resolveTariff(any())).thenReturn(res);

        MockHttpServletRequestBuilder req = get("/wits/tariff")
                .param("importer", "BGR")
                .param("exporter", "CHN")
                .param("hs6", "271012")
                .param("date", "2025-10-14")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratePercent").value(5.0))
                .andExpect(jsonPath("$.basis").value("mfn"))
                .andExpect(jsonPath("$.sourceNote").value("test"));

        verify(witsService).resolveTariff(any());
        verifyNoMoreInteractions(witsService);
    }
}
