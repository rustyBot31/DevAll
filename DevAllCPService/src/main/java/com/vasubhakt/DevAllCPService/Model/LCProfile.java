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
    private Integer problemSolved;
    private Integer contestRating;

    private List<ContestParticipation> contestHistory;
    private Map<String, Integer> submissionHeatMap;
}
