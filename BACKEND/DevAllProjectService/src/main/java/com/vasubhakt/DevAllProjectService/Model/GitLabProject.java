package com.vasubhakt.DevAllProjectService.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitLabProject {
    private Integer id;
    private String name;
    private String description;
    private Integer stars;
    private Integer forks;
    private List<String> topics;
    private String projectUrl;
}
