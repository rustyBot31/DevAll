package com.vasubhakt.DevAllCPService.Service;

import com.vasubhakt.DevAllCPService.Model.ACProfile;
import com.vasubhakt.DevAllCPService.Model.CCProfile;
import com.vasubhakt.DevAllCPService.Model.CFProfile;
import com.vasubhakt.DevAllCPService.Model.LCProfile;

public interface ExternalAPIService {
    
    public CFProfile fetchCfProfile(String handle);
    public LCProfile fetchLcProfile(String handle);
    public CCProfile fetchCcProfile(String handle);
    public ACProfile fetchAcProfile(String handle);
}
