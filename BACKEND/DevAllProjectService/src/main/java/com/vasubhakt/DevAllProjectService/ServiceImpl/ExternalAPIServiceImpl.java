package com.vasubhakt.DevAllProjectService.ServiceImpl;

import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllProjectService.Fetch.GitHubFetch;
import com.vasubhakt.DevAllProjectService.Fetch.GitLabFetch;
import com.vasubhakt.DevAllProjectService.Fetch.HuggingFaceFetch;
import com.vasubhakt.DevAllProjectService.Model.GitHubProfile;
import com.vasubhakt.DevAllProjectService.Model.GitLabProfile;
import com.vasubhakt.DevAllProjectService.Model.HuggingFaceProfile;
import com.vasubhakt.DevAllProjectService.Service.ExternalAPIService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalAPIServiceImpl implements ExternalAPIService {

    private final GitHubFetch gitHubFetch;
    private final HuggingFaceFetch huggingFaceFetch;
    private final GitLabFetch gitLabFetch;
    
    @Override
    public GitHubProfile fetchGitHubProfile(String username) {
        return gitHubFetch.fetchProfile(username);
    }

    @Override
    public HuggingFaceProfile fetchHuggingFaceProfile(String username) {
        return huggingFaceFetch.fetchProfile(username);
    }

    @Override
    public GitLabProfile fetchGitLabProfile(String username) {
        return gitLabFetch.fetchProfile(username);
    }
}
