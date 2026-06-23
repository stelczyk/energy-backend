package com.energymix.backend.model;

import java.util.List;

public record GenerationResponse(
          List<GenerationInterval> data
) { }
