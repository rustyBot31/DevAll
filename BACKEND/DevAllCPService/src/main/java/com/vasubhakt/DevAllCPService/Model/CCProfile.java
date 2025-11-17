package com.vasubhakt.DevAllCPService.Model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CCProfile {
    private String handle;
    private Integer rating;
    private Integer maxRating;
    private Integer stars;
    private Integer problemSolved;

    private List<ContestParticipation> contestHistory;
    private Map<String, Integer> submissionHeatMap; 
}
