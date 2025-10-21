package com.vasubhakt.DevAllCPService.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor

public class ContestParticipation {

    private String contestId;
    private String contestName;
    private Integer rank;
    private Integer oldRating;
    private Integer newRating;
    private String date;
    
}
