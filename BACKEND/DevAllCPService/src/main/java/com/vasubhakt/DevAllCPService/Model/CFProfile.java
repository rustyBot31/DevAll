package com.vasubhakt.DevAllCPService.Model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor

public class CFProfile {
    private String handle;
    private Integer rating;
    private Integer maxRating;
    private Map<String, Integer> submissionHeatMap; // date (YYYY-MM-DD) to number of submissions
    private List<ContestParticipation> contestHistory;
}
