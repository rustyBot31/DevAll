package com.vasubhakt.DevAllProjectService.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitLabProfile {
    private String username;
    private String name;
    private String avatarUrl;
    private String profileUrl;
    private String bio;
    private List<GitLabProject> projects;
}
