package com.vasubhakt.DevAllCPService.Fetch;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.vasubhakt.DevAllCPService.Model.CFProfile;
import com.vasubhakt.DevAllCPService.Model.ContestParticipation;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CFFetch {

    private final RestTemplate restTemplate;
    private final ExecutorService executor;

    @CircuitBreaker(name = "cfbreaker", fallbackMethod = "cfFallback")
    @Retry(name = "cfretry", fallbackMethod = "cfFallback")
    @RateLimiter(name = "cfratelimiter", fallbackMethod = "cfFallback")
    public CFProfile fetchProfile(String handle) {
        try {
            // Run all calls in parallel
            CompletableFuture<Map<?, ?>> userFuture = CompletableFuture.supplyAsync(() -> restTemplate
                    .getForObject("https://codeforces.com/api/user.info?handles=" + handle, Map.class), executor);

            CompletableFuture<Map<?, ?>> contestFuture = CompletableFuture.supplyAsync(() -> restTemplate
                    .getForObject("https://codeforces.com/api/user.rating?handle=" + handle, Map.class), executor);

            CompletableFuture<Map<?, ?>> submissionsFuture = CompletableFuture.supplyAsync(() -> restTemplate
                    .getForObject("https://codeforces.com/api/user.status?handle=" + handle + "&from=1&count=2000",
                            Map.class),
                    executor);

            CompletableFuture.allOf(userFuture, contestFuture, submissionsFuture).join();

            // Extract and build response
            Map<?, ?> userResp = userFuture.get();
            Map<?, ?> user = ((List<Map<?, ?>>) userResp.get("result")).get(0);
            Integer rating = user.get("rating") != null ? ((Number) user.get("rating")).intValue() : 0;
            Integer maxRating = user.get("maxRating") != null ? ((Number) user.get("maxRating")).intValue() : 0;

            Map<?, ?> contestResp = contestFuture.get();
            List<ContestParticipation> contestHistory = new ArrayList<>();
            if ("OK".equals(contestResp.get("status"))) {
                @SuppressWarnings("unchecked")
                List<Map<?, ?>> contests = (List<Map<?, ?>>) contestResp.get("result");
                contestHistory = contests.stream().map(c -> new ContestParticipation(
                        String.valueOf(c.get("contestId")),
                        (String) c.get("contestName"),
                        ((Number) c.get("rank")).intValue(),
                        ((Number) c.get("oldRating")).intValue(),
                        ((Number) c.get("newRating")).intValue(),
                        Instant.ofEpochSecond(((Number) c.get("ratingUpdateTimeSeconds")).longValue())
                                .atZone(ZoneOffset.UTC).toLocalDate().toString()))
                        .collect(Collectors.toList());
            }

            Map<?, ?> submissionsResp = submissionsFuture.get();
            Map<String, Integer> submissionHeatMap = new LinkedHashMap<>();
            if ("OK".equals(submissionsResp.get("status"))) {
                @SuppressWarnings("unchecked")
                List<Map<?, ?>> submissions = (List<Map<?, ?>>) submissionsResp.get("result");

                LocalDate oneYearAgo = LocalDate.now(ZoneOffset.UTC).minusYears(1);
                submissions.sort(Comparator.comparingLong(s -> ((Number) s.get("creationTimeSeconds")).longValue()));

                for (Map<?, ?> sub : submissions) {
                    long epoch = ((Number) sub.get("creationTimeSeconds")).longValue();
                    LocalDate date = Instant.ofEpochSecond(epoch).atZone(ZoneOffset.UTC).toLocalDate();
                    if (date.isBefore(oneYearAgo))
                        continue;
                    submissionHeatMap.merge(date.toString(), 1, Integer::sum);
                }
                submissionHeatMap = submissionHeatMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> b,
                                LinkedHashMap::new));
            }

            return new CFProfile(handle, rating, maxRating, submissionHeatMap, contestHistory);

        } catch (IllegalArgumentException e) {
            throw e; // custom invalid handle
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Codeforces data for " + handle, e);
        }
    }

    public CFProfile cfFallback(String handle, Throwable t) {
        log.warn("Codeforces fetch failed for {}: {}", handle, t.getMessage());
        return null; // cleanly handled in the consumer
    }
}
