package com.cs203.grp2.Asg2;

import com.cs203.grp2.Asg2.controller.PetroleumController;
import com.cs203.grp2.Asg2.models.Petroleum;
import com.cs203.grp2.Asg2.service.PetroleumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PetroleumControllerTest {

    @Mock
    private PetroleumService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new PetroleumController(service)).build();
    }

    @Test
    void getAll_returns200() throws Exception {
        // Use mocks instead of constructors to avoid model ctor issues
        when(service.getAllPetroleum()).thenReturn(
                Arrays.asList(mock(Petroleum.class), mock(Petroleum.class)));

        MockHttpServletRequestBuilder req = get("/petroleum").accept(MediaType.APPLICATION_JSON);

        mvc.perform(req).andExpect(status().isOk());
        verify(service, times(2)).getAllPetroleum();
        verifyNoMoreInteractions(service);
    }

    @Test
    void getByHsCode_valid_returns200() throws Exception {
        when(service.getPetroleumByHsCode("2710")).thenReturn(mock(Petroleum.class));

        mvc.perform(get("/petroleum/2710").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).getPetroleumByHsCode("2710");
        verifyNoMoreInteractions(service);
    }
}
