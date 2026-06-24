package com.energymix.backend.service;

import com.energymix.backend.client.CarbonIntensityClient;
import com.energymix.backend.model.DailyEnergyMix;
import com.energymix.backend.model.FuelSource;
import com.energymix.backend.model.GenerationInterval;
import com.energymix.backend.model.OptimalWindow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnergyServiceTest {

    @Mock
    private CarbonIntensityClient client;

    @InjectMocks
    private EnergyService service;

    private GenerationInterval makeInterval(String from, String to, double wind, double solar,
                                            double nuclear, double hydro,
                                            double biomass, double gas){
        return new GenerationInterval(from, to, List.of(
                new FuelSource("wind", wind),
                new FuelSource("solar", solar),
                new FuelSource("nuclear", nuclear),
                new FuelSource("hydro", hydro),
                new FuelSource("biomass", biomass),
                new FuelSource("gas", gas)
        ));
    }

    @Test
    void shouldCalculateCleanEnergyPercent(){
        GenerationInterval interval = makeInterval("2026-06-23T00:00Z", "2026-06-23T00:30Z",
                20.0, 10.0, 15.0, 5.0, 5.0, 45.0
        );

        when(client.getGeneration(any(), any())).thenReturn(List.of(interval));

        List<DailyEnergyMix> result = service.getEnergyMixForThreeDays();

        assertEquals(55.0, result.get(0).cleanEnergyPercent());
    }

    @Test
    void shouldConvertHoursToIntervals(){
        List<GenerationInterval> intervals = List.of(
                makeInterval("2026-06-24T00:00Z", "2026-06-24T00:30Z", 10, 0, 10, 0, 0, 80),
                makeInterval("2026-06-24T00:30Z", "2026-06-24T01:00Z", 10, 0, 10, 0, 0, 80),
                makeInterval("2026-06-24T01:00Z", "2026-06-24T01:30Z", 30, 0, 30, 0, 0, 40),
                makeInterval("2026-06-24T01:30Z", "2026-06-24T02:00Z", 30, 0, 30, 0, 0, 40)
        );
        when(client.getGeneration(any(), any())).thenReturn(intervals);

        OptimalWindow result = service.findOptimalChargingWindow(1);

        assertEquals(60.0, result.cleanEnergyPercent());
        assertEquals("2026-06-24T01:00Z", result.start());
        assertEquals("2026-06-24T02:00Z", result.end());
    }

    @Test
    void shouldFindWindowWithHighestCleanEnergy() {
        List<GenerationInterval> intervals = List.of(
                makeInterval("2026-06-24T00:00Z", "2026-06-24T00:30Z", 10, 0, 10, 0, 0, 80),
                makeInterval("2026-06-24T00:30Z", "2026-06-24T01:00Z", 40, 0, 40, 0, 0, 20),
                makeInterval("2026-06-24T01:00Z", "2026-06-24T01:30Z", 40, 0, 40, 0, 0, 20),
                makeInterval("2026-06-24T01:30Z", "2026-06-24T02:00Z", 10, 0, 10, 0, 0, 80)
        );
        when(client.getGeneration(any(), any())).thenReturn(intervals);

        OptimalWindow result = service.findOptimalChargingWindow(1);

        assertEquals(80.0, result.cleanEnergyPercent());
    }

}
