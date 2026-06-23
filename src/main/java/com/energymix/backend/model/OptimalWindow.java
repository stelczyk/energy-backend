package com.energymix.backend.model;

public record OptimalWindow(
        String start,
        String end,
        double cleanEnergyPercent
) {}
