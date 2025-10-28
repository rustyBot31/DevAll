package com.vasubhakt.DevAllCPService.Fetch;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            Future<JsonNode> statsFuture = executor.submit(() -> getUserStats(handle));
            Future<JsonNode> contestFuture = executor.submit(() -> getContestHistory(handle));
            Future<JsonNode> heatmapFuture = executor.submit(() -> getSubmissionCalendar(handle));

            JsonNode stats = statsFuture.get();
            JsonNode contests = contestFuture.get();
            JsonNode heatmap = heatmapFuture.get();

            // Extract Problem Stats
            JsonNode profile = stats.path("data").path("matchedUser").path("profile");
            JsonNode submitStats = stats.path("data").path("matchedUser").path("submitStatsGlobal");

            Integer totalSolved = submitStats.path("acSubmissionNum").get(0).path("count").asInt();
            Integer easySolved = submitStats.path("acSubmissionNum").get(1).path("count").asInt();
            Integer mediumSolved = submitStats.path("acSubmissionNum").get(2).path("count").asInt();
            Integer hardSolved = submitStats.path("acSubmissionNum").get(3).path("count").asInt();
            Double acceptanceRate = profile.path("acceptanceRate").asDouble(0.0);

            //Extract contest data
            List<ContestParticipation> contestList = new ArrayList<>();
            JsonNode contestHistory = contests.path("data").path("userContestRankingHistory");
            if (contestHistory.isArray()) {
                for (JsonNode c : contestHistory) {
                    if (c.path("attended").asBoolean()) {
                        contestList.add(new ContestParticipation(
                            c.path("contest").path("titleSlug").asText(),
                            c.path("contest").path("title").asText(),
                            c.path("ranking").asInt(),
                            c.path("rating").asInt() - c.path("ratingProgress").asInt(),
                            c.path("rating").asInt(),
                            c.path("contest").path("startTime").asText()
                        ));
                    }
                }
            } 
            Integer contestRating = null;
            if(!contestList.isEmpty()) {
                contestRating = contestList.getLast().getNewRating();
            }

            // Submission Heatmap
            Map<String, Integer> submissionHeatMap = new HashMap<>();
            JsonNode heatmapNode = heatmap.path("data").path("userProfileUserQuestionSubmitStats").path("submissionCalendar");

            if(!heatmapNode.isMissingNode()) {
                String jsonString = heatmapNode.asText("{}");
                Map<String, String> epochMap = MAPPER.readValue(jsonString, Map.class);
                for(Map.Entry<String, String> e : epochMap.entrySet()) {
                    long epoch = Long.parseLong(e.getKey());
                    String date = Instant.ofEpochSecond(epoch).atZone(ZoneOffset.UTC).toLocalDate().toString();
                    submissionHeatMap.put(date, Integer.parseInt(e.getValue()));
                }
            }

            //Build final object
            LCProfile lcProfile = new LCProfile(
                handle, totalSolved, easySolved, mediumSolved, hardSolved, acceptanceRate, contestRating, contestList, submissionHeatMap
            );

            return lcProfile;
        } catch (IllegalArgumentException e) {
            throw e; // custom invalid handle
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch LeetCode data for " + handle, e);
        }
    }

    private JsonNode getUserStats(String handle) throws Exception {
        String query = """
            {"query":"{ matchedUser(username: \\"%s\\") { profile { ranking acceptanceRate } submitStatsGlobal { acSubmissionNum { count } } } }"}
        """.formatted(handle);

        return postGraphQL(query);
    }

    private JsonNode getContestHistory(String handle) throws Exception {
        String query = """
            {"query":"{ userContestRanking(username: \\"%s\\") { rating } userContestRankingHistory(username: \\"%s\\") { attended rating ranking ratingProgress contest { title titleSlug startTime } } }"}
        """.formatted(handle, handle);

        return postGraphQL(query);
    }

    private JsonNode getSubmissionCalendar(String handle) throws Exception {
        String query = """
            {"query":"{ userProfileUserQuestionSubmitStats(userSlug: \\"%s\\") { submissionCalendar } }"}
        """.formatted(handle);
        return postGraphQL(query);
    }

    private JsonNode postGraphQL(String query) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(query, headers);
        String response = restTemplate.postForObject(LEETCODE_GRAPHQL, request, String.class);

        return new ObjectMapper().readTree(response);
    }
}
