package com.vasubhakt.DevAllCPService.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ACProfile {
    private String handle;
    private Integer rating;
    private Integer maxRating;

    private List<ContestParticipation> contestHistory;
}
