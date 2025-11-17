package com.vasubhakt.DevAllCPService.Service;

import com.vasubhakt.DevAllCPService.Model.CpProfile;

public interface CPService {
    CpProfile getUserProfile(String username);
    void createUserProfile(String username);
    // CpProfile updateUserProfile(String username, CpProfile updatedProfile); //For testing
    CpProfile deleteUserProfile(String username);

}
