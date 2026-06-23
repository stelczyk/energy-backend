package com.energymix.backend.model;

import java.util.List;

public record GenerationInterval(
        String from,
        String to,
        List<FuelSource> generationmix

) {}
