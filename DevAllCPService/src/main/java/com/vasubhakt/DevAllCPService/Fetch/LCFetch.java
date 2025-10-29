package com.vasubhakt.DevAllCPService.Fetch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vasubhakt.DevAllCPService.Model.ContestParticipation;
import com.vasubhakt.DevAllCPService.Model.LCProfile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LCFetch {

    private final RestTemplate restTemplate;
    private final ExecutorService executor;

    private static final String LEETCODE_GRAPHQL = "https://leetcode.com/graphql";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public LCProfile fetchProfile(String handle) {
        try {
            // All calls in parallel
            Future<JsonNode> statsFuture = executor.submit(() -> getUserStats(handle));
            Future<JsonNode> contestFuture = executor.submit(() -> getContestHistory(handle));

            JsonNode stats = statsFuture.get();
            JsonNode contests = contestFuture.get();

            // Extract Problem Stats
            JsonNode submitStats = stats.path("data").path("matchedUser").path("submitStatsGlobal");

            Integer totalSolved = submitStats.path("acSubmissionNum").get(0).path("count").asInt();
            Integer easySolved = submitStats.path("acSubmissionNum").get(1).path("count").asInt();
            Integer mediumSolved = submitStats.path("acSubmissionNum").get(2).path("count").asInt();
            Integer hardSolved = submitStats.path("acSubmissionNum").get(3).path("count").asInt();

            // Extract contest data
            List<ContestParticipation> contestList = new ArrayList<>();
            JsonNode contestHistory = contests.path("data").path("userContestRankingHistory");
            if (contestHistory.isArray()) {
                for (JsonNode c : contestHistory) {
                    if (c.path("attended").asBoolean()) {
                        contestList.add(new ContestParticipation(
                                c.path("contest").path("titleSlug").asText(),
                                c.path("contest").path("title").asText(),
                                c.path("ranking").asInt(),
                                null, // Old rating no longer available from api
                                c.path("rating").asInt(),
                                c.path("contest").path("startTime").asText()));
                    }
                }
            }
            Integer contestRating = null;
            if (!contestList.isEmpty()) {
                contestRating = contestList.getLast().getNewRating();
            }

            // Submission Heatmap
            // Not available since LeetCode GraphQL API does not provide suitable API

            // Build final object
            LCProfile lcProfile = new LCProfile(
                    handle, totalSolved, easySolved, mediumSolved, hardSolved, contestRating, contestList,
                    Collections.emptyMap());

            return lcProfile;
        } catch (IllegalArgumentException e) {
            throw e; // custom invalid handle
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private JsonNode getUserStats(String handle) throws Exception {
        String query = """
                    {"query":"{ matchedUser(username: \\"%s\\") { profile { ranking } submitStatsGlobal { acSubmissionNum { count } } } }"}
                """
                .formatted(handle);

        return postGraphQL(query);
    }

    private JsonNode getContestHistory(String handle) throws Exception {
        String query = """
                    {"query":"{ userContestRanking(username: \\"%s\\") { rating } userContestRankingHistory(username: \\"%s\\") { attended rating ranking contest { title titleSlug startTime } } }"}
                """
                .formatted(handle, handle);

        return postGraphQL(query);
    }

    private JsonNode postGraphQL(String query) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        headers.add("Referer", "https://leetcode.com/");

        HttpEntity<String> request = new HttpEntity<>(query, headers);
        String response = restTemplate.postForObject(LEETCODE_GRAPHQL, request, String.class);

        return MAPPER.readTree(response);
    }

}
