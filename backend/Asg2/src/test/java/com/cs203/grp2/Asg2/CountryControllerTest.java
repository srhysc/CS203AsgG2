package com.cs203.grp2.Asg2;

import com.cs203.grp2.Asg2.controller.CountryController;
import com.cs203.grp2.Asg2.models.Country;
import com.cs203.grp2.Asg2.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
public class CountryControllerTest {

    @Mock
    private CountryService svc;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CountryController(svc)).build();
    }

    @Test
    void getAllCountries_returns200() throws Exception {
        when(svc.getAll()).thenReturn(List.of(new Country(), new Country()));
        MockHttpServletRequestBuilder req = get("/countries").accept(MediaType.APPLICATION_JSON);
        mvc.perform(req).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        verify(svc).getAll();
        verifyNoMoreInteractions(svc);
    }

    @Test
    void getCountryByCode_valid_callsServiceWithStringParam() throws Exception {
        when(svc.getCountryByCode("123")).thenReturn(new Country());
        mvc.perform(get("/countries/123").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        ArgumentCaptor<String> cap = ArgumentCaptor.forClass(String.class);
        verify(svc).getCountryByCode(cap.capture());
        verifyNoMoreInteractions(svc);
        org.junit.jupiter.api.Assertions.assertEquals("123", cap.getValue());
    }
}
