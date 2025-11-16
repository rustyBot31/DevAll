package com.vasubhakt.DevAllPortfolioService.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    private String name;
    private String description;
    private String projectLink;
    private String gitHubLink;
    private List<String> techStack;
    private List<String> domains; 
}
