package com.vasubhakt.DevAllCPService.ServiceImpl;

import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Fetch.CFFetch;
import com.vasubhakt.DevAllCPService.Model.ACProfile;
import com.vasubhakt.DevAllCPService.Model.CCProfile;
import com.vasubhakt.DevAllCPService.Model.CFProfile;
import com.vasubhakt.DevAllCPService.Model.LCProfile;
import com.vasubhakt.DevAllCPService.Service.ExternalAPIService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalAPIServiceImpl implements ExternalAPIService {

    private final CFFetch cfFetch;

    @Override
    public CFProfile fetchCfProfile(String handle) {
        return cfFetch.fetchProfile(handle);
    }

    @Override
    public LCProfile fetchLcProfile(String handle) {
        return new LCProfile();
    }

    @Override
    public CCProfile fetchCcProfile(String handle) {
        return new CCProfile();
    }

    @Override
    public ACProfile fetchAcProfile(String handle) {
        return new ACProfile();
    }

}
