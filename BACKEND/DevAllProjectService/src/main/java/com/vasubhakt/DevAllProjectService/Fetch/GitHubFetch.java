package com.vasubhakt.DevAllProjectService.Fetch;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

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

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
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

    @CircuitBreaker(name = "githubbreaker", fallbackMethod = "gitHubFallback")
    @Retry(name = "githubretry", fallbackMethod = "gitHubFallback")
    @RateLimiter(name = "githubratelimiter", fallbackMethod = "gitHubFallback")
    public GitHubProfile fetchProfile(String username) {
        try {
            CompletableFuture<Map<?, ?>> profileFuture = CompletableFuture
                    .supplyAsync(() -> restTemplate.getForObject(BASE_URL + username, Map.class), executor);

            CompletableFuture<List<Map<String, Object>>> repoFuture = CompletableFuture
                    .supplyAsync(() -> fetchPinnedRepos(username), executor);

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

            List<Map<String, Object>> pinnedRepos = repoFuture.get();

            List<GitHubRepo> repos = pinnedRepos.stream().map(r -> {
                String repoName = (String) r.get("name");
                String fullName = username + "/" + repoName; // Pinned repos do not return full_name
                String description = (String) r.get("description");
                Integer stars = (Integer) r.get("stargazerCount");
                Integer forks = (Integer) r.get("forkCount");
                String license = (String) r.get("license");
                String repoUrl = (String) r.get("url");
                List<String> topics = (List<String>) r.get("topics");
                Map<String, Integer> languageStats = fetchLanguages(fullName);

                return new GitHubRepo(
                        repoName,
                        fullName,
                        description,
                        stars,
                        forks,
                        license,
                        repoUrl,
                        topics,
                        languageStats);
            }).toList();

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

    private List<Map<String, Object>> fetchPinnedRepos(String username) {
        try {
            String query = """
                    {
                      user(login: "%s") {
                        pinnedItems(first: 6, types: REPOSITORY) {
                          nodes {
                            ... on Repository {
                              name
                              description
                              url
                              stargazerCount
                              forkCount
                              licenseInfo { name }
                              repositoryTopics(first: 10) {
                                nodes {
                                  topic { name }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                    """.formatted(username);

            String json = """
                    {
                      "query": "%s"
                    }
                    """.formatted(query.replace("\"", "\\\"").replace("\n", " ")); 

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(TOKEN);

            HttpEntity<String> req = new HttpEntity<>(json, headers);
            String res = restTemplate.postForObject(GRAPHQL_URL, req, String.class);

            JsonNode root = MAPPER.readTree(res)
                    .path("data").path("user").path("pinnedItems").path("nodes");

            List<Map<String, Object>> list = new ArrayList<>();

            for (JsonNode node : root) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", node.path("name").asText());
                map.put("description", node.path("description").asText());
                map.put("url", node.path("url").asText());
                map.put("stargazerCount", node.path("stargazerCount").asInt());
                map.put("forkCount", node.path("forkCount").asInt());
                map.put("license", node.path("licenseInfo").path("name").asText(null));

                List<String> topics = new ArrayList<>();
                for (JsonNode t : node.path("repositoryTopics").path("nodes")) {
                    topics.add(t.path("topic").path("name").asText());
                }
                map.put("topics", topics);

                list.add(map);
            }

            return list;

        } catch (Exception e) {
            log.error("Pinned repo fetch error: {}", e.getMessage());
            return List.of();
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

    public GitHubProfile gitHubFallback(String username, Throwable t) {
        log.warn("GitHub fetch failed for {}: {}", username, t.getMessage());
        return null;
    }
}
