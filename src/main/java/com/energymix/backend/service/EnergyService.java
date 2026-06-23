package com.energymix.backend.service;

import com.energymix.backend.client.CarbonIntensityClient;
import com.energymix.backend.model.DailyEnergyMix;
import com.energymix.backend.model.GenerationInterval;
import com.energymix.backend.model.OptimalWindow;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EnergyService{
    private static final Set<String> CLEAN_SOURCES = Set.of("biomass", "nuclear", "hydro", "wind", "solar");

    private final CarbonIntensityClient client;

    public EnergyService(CarbonIntensityClient client){
        this.client = client;
    }

    public List<DailyEnergyMix> getEnergyMixForThreeDays(){
        LocalDate today = LocalDate.now();
        List<DailyEnergyMix> result = new ArrayList<>();

        for (int i = 0; i< 3;i++){
            LocalDate date = today.plusDays(i);
            String from = date + "T00:00Z";
            String to = date + "T23:30Z";

            List<GenerationInterval> intervals = client.getGeneration(from, to);
            result.add(aggregateDay(date.toString(),intervals));
        }
        return result;
    }

    private DailyEnergyMix aggregateDay(String date, List<GenerationInterval> intervals){
        // flatten all sources from all intervals into a single list and group by name,
        // calculating the average percentage for each
        Map<String, Double> sources = intervals.stream()
                .flatMap(i -> i.generationmix().stream())
                .collect(Collectors.groupingBy(
                        f -> f.fuel(),Collectors.averagingDouble(f-> f.perc())
                ));

        double cleanPercent = sources.entrySet().stream()
                .filter(e -> CLEAN_SOURCES.contains(e.getKey()))
                .mapToDouble(Map.Entry::getValue)
                .sum();

        return new DailyEnergyMix(
                date,
                sources,
                Math.round(cleanPercent * 10.0) / 10.0
        );
    }

    public OptimalWindow findOptimalChargingWindow(int hours){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate dayAfter = LocalDate.now().plusDays(2);

        String from = tomorrow + "T00:00Z";
        String to = dayAfter + "T23:30Z";

        List<GenerationInterval> intervals = client.getGeneration(from, to);

        int windowSize = hours * 2;
        int bestStart = 0;
        double bestClean = 0;

        for(int i = 0; i<intervals.size() - windowSize; i++){
            List<GenerationInterval> window = intervals.subList(i, i+windowSize);

            double cleanPercent = window.stream()
                    .mapToDouble(this::calculateCleanPercent)
                    .average()
                    .orElse(0);

            if(cleanPercent > bestClean){
                bestClean = cleanPercent;
                bestStart = i;
            }
        }
        GenerationInterval start = intervals.get(bestStart);
        GenerationInterval end = intervals.get(bestStart + windowSize - 1);

        return new OptimalWindow(
                start.from(),
                end.to(),
                Math.round(bestClean * 10.0) / 10.0
        );
    }

    private double calculateCleanPercent(GenerationInterval interval) {
        // sum percentages of clean fuel only for given interval
        return interval.generationmix().stream()
                .filter(f -> CLEAN_SOURCES.contains(f.fuel()))
                .mapToDouble(f -> f.perc())
                .sum();
    }

}