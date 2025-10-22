package com.vasubhakt.DevAllCPService.Service;

import com.vasubhakt.DevAllCPService.Model.CpProfile;

public interface CPService {
    CpProfile getUserProfile(String id);
    CpProfile updateUserProfile(String id);
}
