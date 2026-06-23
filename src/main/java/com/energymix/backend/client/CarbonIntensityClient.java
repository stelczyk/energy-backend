package com.energymix.backend.client;

import com.energymix.backend.model.GenerationInterval;
import com.energymix.backend.model.GenerationResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CarbonIntensityClient {

    private static final String BASE_URL = "https://api.carbonintensity.org.uk";

    private final RestClient restClient;

    public CarbonIntensityClient(){
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public List<GenerationInterval> getGeneration(String from, String to) {
        GenerationResponse response = restClient.get()
                .uri("/generation/" + from + "/" + to)
                .retrieve()
                .body(GenerationResponse.class);

        return response.data();
    }
}
