package com.vasubhakt.DevAllProjectService.Model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GitHubProfile {
    
    private String username;
    private String name;
    private String bio;
    private Integer publicRepos;
    private Integer followers;
    private Integer following;
    private String profileUrl;
    private String avatarUrl;

    List<GitHubRepo> pinnedRepositories;
    Map<String, Integer> contributionHeatMap; 
}
