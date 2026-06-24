package com.energymix.backend.controller;

import com.energymix.backend.model.DailyEnergyMix;
import com.energymix.backend.model.OptimalWindow;
import com.energymix.backend.service.EnergyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnergyController.class)
class EnergyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnergyService energyService;

    @Test
    void shouldReturn200ForEnergyMix() throws Exception {
        when(energyService.getEnergyMixForThreeDays())
                .thenReturn(List.of(
                        new DailyEnergyMix("2026-06-23", Map.of("wind", 27.8), 65.0)
                ));

        mockMvc.perform(get("/api/energy-mix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2026-06-23"))
                .andExpect(jsonPath("$[0].cleanEnergyPercent").value(65.0));
    }

    @Test
    void shouldReturn200ForOptimalWindow() throws Exception {
        when(energyService.findOptimalChargingWindow(3))
                .thenReturn(new OptimalWindow(
                        "2026-06-24T14:00Z",
                        "2026-06-24T17:00Z",
                        71.2
                ));

        mockMvc.perform(get("/api/optimal-window").param("hours", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start").value("2026-06-24T14:00Z"))
                .andExpect(jsonPath("$.cleanEnergyPercent").value(71.2));
    }

    @Test
    void shouldReturn400WhenHoursTooLow() throws Exception {
        mockMvc.perform(get("/api/optimal-window").param("hours", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenHoursTooHigh() throws Exception {
        mockMvc.perform(get("/api/optimal-window").param("hours", "7"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenHoursMissing() throws Exception {
        mockMvc.perform(get("/api/optimal-window"))
                .andExpect(status().isBadRequest());
    }
}