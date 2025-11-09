package com.vasubhakt.DevAllProjectService.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuggingFaceProfile {
    private String username;
    private String profileUrl;

    private List<HuggingFaceModel> models;
    private List<HuggingFaceDataset> datasets;
    private List<HuggingFaceSpace> spaces;
}
