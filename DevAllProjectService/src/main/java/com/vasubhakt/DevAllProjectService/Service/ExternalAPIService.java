package com.vasubhakt.DevAllProjectService.Service;

import com.vasubhakt.DevAllProjectService.Model.GitHubProfile;

public interface ExternalAPIService {
    
    public GitHubProfile fetchGitHubProfile(String username);
}
