package com.vasubhakt.DevAllProjectService.Fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vasubhakt.DevAllProjectService.Model.HuggingFaceDataset;
import com.vasubhakt.DevAllProjectService.Model.HuggingFaceModel;
import com.vasubhakt.DevAllProjectService.Model.HuggingFaceProfile;
import com.vasubhakt.DevAllProjectService.Model.HuggingFaceSpace;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HuggingFaceFetch {

    private final RestTemplate restTemplate;
    private final ExecutorService executor;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String BASE_URL = "https://huggingface.co/api/";

    @CircuitBreaker(name = "huggingfacebreaker", fallbackMethod = "huggingfaceFallback")
    @Retry(name = "huggingfaceretry", fallbackMethod = "huggingfaceFallback")
    @RateLimiter(name = "huggingfaceratelimiter", fallbackMethod = "huggingfaceFallback")
    public HuggingFaceProfile fetchProfile(String username) {
        try {
            CompletableFuture<HuggingFaceProfile> profileFuture = CompletableFuture
                    .supplyAsync(() -> fetchUserProfile(username), executor);
            CompletableFuture<List<HuggingFaceModel>> modelsFuture = CompletableFuture
                    .supplyAsync(() -> fetchModels(username), executor);
            CompletableFuture<List<HuggingFaceDataset>> datasetsFuture = CompletableFuture
                    .supplyAsync(() -> fetchDatasets(username), executor);
            CompletableFuture<List<HuggingFaceSpace>> spacesFuture = CompletableFuture
                    .supplyAsync(() -> fetchSpaces(username), executor);

            CompletableFuture.allOf(profileFuture, modelsFuture, datasetsFuture, spacesFuture).join();

            HuggingFaceProfile profile = profileFuture.get();
            profile.setModels(modelsFuture.get());
            profile.setDatasets(datasetsFuture.get());
            profile.setSpaces(spacesFuture.get());

            return profile;
        } catch (Exception e) {
            log.error("Failed to fetch Hugging Face data for {}: {}", username, e.getMessage());
            throw new RuntimeException("Failed to fetch Hugging Face data for " + username, e);
        }
    }

    private HuggingFaceProfile fetchUserProfile(String username) {
        try {
            return new HuggingFaceProfile(
                    username,
                    "https://huggingface.co/" + username,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>());
        } catch (Exception e) {
            log.error("Error fetching Hugging Face profile for {}: {}", username, e.getMessage());
            return new HuggingFaceProfile(username, "https://huggingface.co/" + username,
                    List.of(), List.of(), List.of());
        }
    }

    private List<HuggingFaceModel> fetchModels(String username) {
        try {
            String url = BASE_URL + "models?author=" + username + "&sort=lastModified&direction=-1&limit=10";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            JsonNode root = MAPPER.readTree(response.getBody());

            List<HuggingFaceModel> models = new ArrayList<>();
            for (JsonNode m : root) {
                models.add(new HuggingFaceModel(
                        m.path("id").asText(),
                        m.path("modelId").asText(null),
                        m.path("cardData").path("description").asText(null),
                        m.path("library_name").asText(null),
                        extractList(m.path("tags")),
                        m.path("likes").asInt(0),
                        m.path("downloads").asInt(0),
                        "https://huggingface.co/" + m.path("id").asText()));
            }
            return models;
        } catch (Exception e) {
            log.error("Error fetching Hugging Face models for {}: {}", username, e.getMessage());
            return List.of();
        }
    }

    private List<HuggingFaceDataset> fetchDatasets(String username) {
        try {
            String url = BASE_URL + "datasets?author=" + username + "&sort=lastModified&direction=-1&limit=10";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            JsonNode root = MAPPER.readTree(response.getBody());

            List<HuggingFaceDataset> datasets = new ArrayList<>();
            for (JsonNode d : root) {
                datasets.add(new HuggingFaceDataset(
                        d.path("id").asText(),
                        d.path("datasetId").asText(null),
                        d.path("cardData").path("description").asText(null),
                        extractList(d.path("tags")),
                        d.path("likes").asInt(0),
                        d.path("downloads").asInt(0),
                        "https://huggingface.co/datasets/" + d.path("id").asText()));
            }
            return datasets;
        } catch (Exception e) {
            log.error("Error fetching Hugging Face datasets for {}: {}", username, e.getMessage());
            return List.of();
        }
    }

    private List<HuggingFaceSpace> fetchSpaces(String username) {
        try {
            String url = BASE_URL + "spaces?author=" + username + "&sort=lastModified&direction=-1&limit=10";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            JsonNode root = MAPPER.readTree(response.getBody());

            List<HuggingFaceSpace> spaces = new ArrayList<>();
            for (JsonNode s : root) {
                spaces.add(new HuggingFaceSpace(
                    s.path("id").asText(), 
                    s.path("spaceId").asText(null),
                    s.path("sdk").asText(null), 
                    s.path("cardData").path("description").asText(null),
                    extractList(s.path("tags")), 
                    s.path("likes").asInt(0),
                    "https://huggingface.co/spaces/" + s.path("id").asText()));
            }
            return spaces;
        } catch (Exception e) {
            log.error("Error fetching Hugging Face spaces for {}: {}", username, e.getMessage());
            return List.of();
        }
    }

    private static List<String> extractList(JsonNode node) {
        if (node == null || !node.isArray())
            return List.of();
        List<String> list = new ArrayList<>();
        node.forEach(x -> list.add(x.asText()));
        return list;
    }

    public HuggingFaceProfile huggingfaceFallback(String username, Throwable t) {
        log.warn("HuggingFace fetch failed for {}: {}", username, t.getMessage());
        return null;
    }
}
