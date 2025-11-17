package com.vasubhakt.DevAllProjectService.Fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vasubhakt.DevAllProjectService.Model.GitLabProfile;
import com.vasubhakt.DevAllProjectService.Model.GitLabProject;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitLabFetch {

    private final RestTemplate restTemplate;
    private final ExecutorService executor;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String BASE_URL = "https://gitlab.com/api/v4/";

    @CircuitBreaker(name = "gitlabbreaker", fallbackMethod = "gitLabFallback")
    @Retry(name = "gitlabretry", fallbackMethod = "gitLabFallback")
    @RateLimiter(name = "gitlabratelimiter", fallbackMethod = "gitLabFallback")
    public GitLabProfile fetchProfile(String username) {
        try {
            // Step 1: Fetch user info
            ResponseEntity<String> userResponse = restTemplate.exchange(
                    BASE_URL + "users?username=" + username,
                    HttpMethod.GET, null, String.class);

            JsonNode users = MAPPER.readTree(userResponse.getBody());
            if (users.isEmpty()) {
                log.warn("GitLab user not found: {}", username);
                return null;
            }

            JsonNode user = users.get(0);
            int userId = user.path("id").asInt();
            String name = user.path("name").asText();
            String avatar = user.path("avatar_url").asText();
            String webUrl = user.path("web_url").asText();
            String bio = user.path("bio").asText(null);

            // Step 2: Fetch projects (async)
            CompletableFuture<List<GitLabProject>> projectsFuture =
                    CompletableFuture.supplyAsync(() -> fetchProjects(userId), executor);

            List<GitLabProject> projects = projectsFuture.get();

            return new GitLabProfile(username, name, avatar, webUrl, bio, projects);

        } catch (Exception e) {
            log.error("Error fetching GitLab profile for {}: {}", username, e.getMessage());
            return null;
        }
    }

    private List<GitLabProject> fetchProjects(int userId) {
        try {
            String url = BASE_URL + "users/" + userId + "/projects?order_by=last_activity_at&sort=desc&per_page=6";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

            JsonNode root = MAPPER.readTree(response.getBody());
            List<GitLabProject> projects = new ArrayList<>();

            for (JsonNode p : root) {
                projects.add(new GitLabProject(
                        p.path("id").asInt(),
                        p.path("name").asText(),
                        p.path("description").asText(null),
                        p.path("star_count").asInt(0),
                        p.path("forks_count").asInt(0),
                        extractList(p.path("topics")),
                        p.path("web_url").asText()
                ));
            }

            return projects;
        } catch (Exception e) {
            log.error("Error fetching GitLab projects for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    private static List<String> extractList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node != null && node.isArray()) {
            node.forEach(x -> list.add(x.asText()));
        }
        return list;
    }

    public GitLabProfile gitLabFallback(String username, Throwable t) {
        log.warn("GitLab fetch failed for {}: {}", username, t.getMessage());
        return null;
    }
}
