package com.vasubhakt.DevAllProjectService.ServiceImpl;

import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllProjectService.Fetch.GitHubFetch;
import com.vasubhakt.DevAllProjectService.Model.GitHubProfile;
import com.vasubhakt.DevAllProjectService.Service.ExternalAPIService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalAPIServiceImpl implements ExternalAPIService {

    private final GitHubFetch gitHubFetch;
    
    @Override
    public GitHubProfile fetchGitHubProfile(String username) {
        return gitHubFetch.fetchProfile(username);
    }
}
