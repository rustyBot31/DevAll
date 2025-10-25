package com.vasubhakt.DevAllCPService.Service;

import com.vasubhakt.DevAllCPService.Model.CpProfile;

public interface CPService {
    CpProfile getUserProfile(String id);
    void createUserProfile(String id);
    CpProfile updateUserProfile(String id, CpProfile updatedProfile);
    CpProfile deleteUserProfile(String id);
}
