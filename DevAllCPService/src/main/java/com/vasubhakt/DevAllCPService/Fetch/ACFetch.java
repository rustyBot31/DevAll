package com.vasubhakt.DevAllCPService.Fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.vasubhakt.DevAllCPService.Model.ACProfile;
import com.vasubhakt.DevAllCPService.Model.ContestParticipation;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ACFetch {
    
    private final RestTemplate restTemplate;
    private final ExecutorService executor;

    private static final String ATCODER_API = "https://atcoder.jp/users/";

    @CircuitBreaker(name = "acbreaker", fallbackMethod = "acFallback")
    @Retry(name = "acretry", fallbackMethod = "acFallback")
    @RateLimiter(name = "acratelimiter", fallbackMethod = "acFallback")
    public ACProfile fetchProfile(String handle) {
        try {
            String url = ATCODER_API + handle + "/history/json";
            Future<JsonNode[]> future = executor.submit(() ->
                restTemplate.getForObject(url, JsonNode[].class)
            );
            JsonNode[] contestsArray = future.get();
            if(contestsArray == null || contestsArray.length == 0) {
                throw new RuntimeException("No contests found or invalid AtCoder handle: " + handle);
            }
            Integer currentRating = 0, maxRating = 0;
            List<ContestParticipation> contests = new ArrayList<>();
            for(JsonNode c : contestsArray) {
                if(!c.path("IsRated").asBoolean()) continue;

                Integer newRating = c.path("NewRating").asInt(0);
                Integer oldRating = c.path("OldRating").asInt(0);
                String contestName = c.path("ContestName").asText();
                String contestCode = c.path("ContestScreenName").asText();
                String endTime = c.path("EndTime").asText();
                Integer rank = c.path("Place").asInt(0);

                contests.add(new ContestParticipation(
                    contestCode,
                    contestName,
                    rank,
                    oldRating,
                    newRating,
                    endTime
                ));

                currentRating = newRating;
                maxRating = Math.max(maxRating, newRating);
            }

            return new ACProfile(
                handle, currentRating, maxRating, contests
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ACProfile acFallback(String handle, Throwable t) {
        log.warn("AtCoder fetch failed for {}: {}", handle, t.getMessage());
        return null; // cleanly handled in the consumer
    }
}
