package com.vasubhakt.DevAllProjectService.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuggingFaceSpace {
    private String id;
    private String name;
    private String sdk;
    private String description;
    private List<String> tags;
    private Integer likes;
    private String spaceUrl;
}
