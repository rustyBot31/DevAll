package com.vasubhakt.DevAllProjectService.Model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GitHubRepo {

    private String name;
    private String fullName;
    private String description;
    private Integer stars;
    private Integer forks;
    private Integer watchers;
    private String license;
    private String repoUrl;
    private List<String> topics;
    
    private Map<String, Integer> languageStats;
    private Integer commits;
    private Integer openIssues;
    private Integer pullRequests;
}
