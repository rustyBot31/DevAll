package com.vasubhakt.DevAllCPService.ServiceImpl;

import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Model.ACProfile;
import com.vasubhakt.DevAllCPService.Model.CCProfile;
import com.vasubhakt.DevAllCPService.Model.CFProfile;
import com.vasubhakt.DevAllCPService.Model.LCProfile;
import com.vasubhakt.DevAllCPService.Service.ExternalAPIService;

@Service
public class ExternalAPIServiceImpl implements ExternalAPIService {
    
    @Override
    public CFProfile fetchCfProfile(String handle) {
        return new CFProfile();
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
