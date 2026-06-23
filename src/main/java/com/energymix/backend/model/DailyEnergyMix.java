package com.energymix.backend.model;

import java.util.Map;

public record DailyEnergyMix(
        String date,
        Map<String,Double> sources,
        double cleanEnergyPercent
) {}
