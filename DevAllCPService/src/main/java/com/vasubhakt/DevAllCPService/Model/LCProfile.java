package com.vasubhakt.DevAllCPService.Model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LCProfile {
    private String handle;

    private Integer totalSolved;
    private Integer easySolved;
    private Integer mediumSolved;
    private Integer hardSolved;

    private Double acceptanceRate;
    private Integer contestRating;

    private List<ContestParticipation> contestHistory;
    private Map<String, Integer> submissionHeatMap; // date -> # submissions
}
