package com.vasubhakt.DevAllProjectService.Service;

import com.vasubhakt.DevAllProjectService.Model.GitHubProfile;
import com.vasubhakt.DevAllProjectService.Model.HuggingFaceProfile;

public interface ExternalAPIService {
    
    public GitHubProfile fetchGitHubProfile(String username);
    public HuggingFaceProfile fetchHuggingFaceProfile(String username);
}
