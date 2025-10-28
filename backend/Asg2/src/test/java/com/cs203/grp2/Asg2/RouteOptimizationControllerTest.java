package com.cs203.grp2.Asg2;

import com.cs203.grp2.Asg2.DTO.RouteBreakdown;
import com.cs203.grp2.Asg2.DTO.RouteOptimizationResponse;
import com.cs203.grp2.Asg2.controller.RouteOptimizationController;
import com.cs203.grp2.Asg2.service.RouteOptimizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RouteOptimizationControllerTest {

    @Mock
    private RouteOptimizationService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new RouteOptimizationController(service)).build();
    }

    @Test
    void calculate_paramsMapped_returns200() throws Exception {
        RouteOptimizationResponse resp = new RouteOptimizationResponse(
                List.of(new RouteBreakdown("CN", List.of("TH"), "SG", 100, 5, 8, 113, 8)));
        when(service.calculateOptimalRoutes(any())).thenReturn(resp);

        MockHttpServletRequestBuilder req = get("/route-optimization/calculate")
                .param("importer", "SG")
                .param("exporter", "123") // numeric exporter scenario
                .param("hsCode", "271012")
                .param("units", "10")
                .param("maxTransits", "1")
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topRoutes").isArray());

        verify(service).calculateOptimalRoutes(any());
        verifyNoMoreInteractions(service);
    }
}
