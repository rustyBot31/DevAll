package com.vasubhakt.DevAllProjectService.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFetchRequest {
    private String username;
    private String platform;
    private String handle;
}
