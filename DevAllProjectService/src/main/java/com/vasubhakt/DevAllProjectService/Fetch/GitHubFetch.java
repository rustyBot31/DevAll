package com.vasubhakt.DevAllProjectService.Fetch;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vasubhakt.DevAllProjectService.Model.GitHubProfile;
import com.vasubhakt.DevAllProjectService.Model.GitHubRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubFetch {

    private final RestTemplate restTemplate;
    private final ExecutorService executor;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String BASE_URL = "https://api.github.com/users/";
    private static final String GRAPHQL_URL = "https://api.github.com/graphql";

    @Value("${github.token}")
    private String TOKEN;

    public GitHubProfile fetchProfile(String username) {
        try {
            CompletableFuture<Map<?, ?>> profileFuture = CompletableFuture
                    .supplyAsync(() -> restTemplate.getForObject(BASE_URL + username, Map.class), executor);

            CompletableFuture<List<Map<?, ?>>> repoFuture = CompletableFuture.supplyAsync(
                    () -> restTemplate.getForObject(BASE_URL + username + "/repos?per_page=6&sort=updated", List.class),
                    executor);

            CompletableFuture<Map<String, Integer>> contribFuture = CompletableFuture
                    .supplyAsync(() -> fetchContributionHeatmap(username), executor);

            CompletableFuture.allOf(profileFuture, repoFuture, contribFuture).join();

            // profile data
            Map<?, ?> profile = profileFuture.get();
            String name = (String) profile.get("name");
            String bio = (String) profile.get("bio");
            Integer publicRepos = ((Number) profile.get("public_repos")).intValue();
            Integer followers = ((Number) profile.get("followers")).intValue();
            Integer following = ((Number) profile.get("following")).intValue();
            String avatarUrl = (String) profile.get("avatar_url");
            String profileUrl = (String) profile.get("html_url");

            // repos data
            List<Map<?, ?>> repoList = repoFuture.get();
            List<GitHubRepo> repos = repoList.stream().limit(6).map(r -> {
                String repoName = (String) r.get("name");
                String fullName = (String) r.get("full_name");
                String description = (String) r.get("description");
                Integer stars = r.get("stargazers_count") instanceof Number
                        ? ((Number) r.get("stargazers_count")).intValue()
                        : 0;
                Integer forks = r.get("forks_count") instanceof Number ? ((Number) r.get("forks_count")).intValue() : 0;
                Integer watchers = r.get("watchers_count") instanceof Number
                        ? ((Number) r.get("watchers_count")).intValue()
                        : 0;
                String license = r.get("license") != null ? ((Map<?, ?>) r.get("license")).get("name").toString()
                        : null;
                String repoUrl = (String) r.get("html_url");
                List<String> topics = r.get("topics") != null ? (List<String>) r.get("topics") : List.of();
                Map<String, Integer> languageStats = fetchLanguages(fullName);

                return new GitHubRepo(repoName, fullName, description, stars, forks, watchers, license, repoUrl, topics,
                        languageStats);
            }).collect(Collectors.toList());

            Map<String, Integer> heatMap = contribFuture.get();

            return new GitHubProfile(username, name, bio, publicRepos, followers, following, profileUrl, avatarUrl,
                    repos, heatMap);
        } catch (IllegalArgumentException e) {
            throw e; // custom invalid handle
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Codeforces data for " + username, e);
        }
    }

    private Map<String, Integer> fetchContributionHeatmap(String username) {
        try {
            LocalDate now = LocalDate.now(ZoneOffset.UTC);
            LocalDate oneYearAgo = now.minusYears(1);

            String fromDate = oneYearAgo.toString() + "T00:00:00Z";
            String toDate = now.toString() + "T00:00:00Z";

            String query = String.format(
                    """
                                {
                                  "query": "{ user(login: \\"%s\\") { contributionsCollection(from: \\"%s\\", to: \\"%s\\") { contributionCalendar { weeks { contributionDays { date contributionCount } } } } } }"
                                }
                            """,
                    username, fromDate, toDate);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (TOKEN != null && !TOKEN.isEmpty()) {
                headers.setBearerAuth(TOKEN);
            }

            HttpEntity<String> request = new HttpEntity<>(query, headers);
            String response = restTemplate.postForObject(GRAPHQL_URL, request, String.class);
            JsonNode root = MAPPER.readTree(response);

            Map<String, Integer> tempMap = new HashMap<>();
            root.path("data").path("user").path("contributionsCollection")
                    .path("contributionCalendar").path("weeks")
                    .forEach(week -> week.path("contributionDays").forEach(day -> {
                        String date = day.path("date").asText();
                        int count = day.path("contributionCount").asInt();
                        tempMap.put(date, count);
                    }));

            return tempMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(LinkedHashMap::new,
                            (m, e) -> m.put(e.getKey(), e.getValue()),
                            LinkedHashMap::putAll);

        } catch (Exception e) {
            log.error("Error fetching contribution heatmap: {}", e.getMessage());
            return Map.of();
        }
    }

    private Map<String, Integer> fetchLanguages(String fullName) {
        try {
            String url = "https://api.github.com/repos/" + fullName + "/languages";
            Map<String, Integer> langs = restTemplate.getForObject(url, Map.class);
            return langs != null ? langs : Map.of();
        } catch (Exception e) {
            log.warn("Failed to fetch languages for {}: {}", fullName, e.getMessage());
            return Map.of();
        }
    }

    public GitHubProfile githubFallback(String username, Throwable t) {
        log.warn("GitHub fetch failed for {}: {}", username, t.getMessage());
        return null;
    }
}
