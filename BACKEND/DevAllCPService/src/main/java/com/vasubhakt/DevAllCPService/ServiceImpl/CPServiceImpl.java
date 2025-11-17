package com.vasubhakt.DevAllCPService.ServiceImpl;

import org.springframework.stereotype.Service;

import com.vasubhakt.DevAllCPService.Model.CpProfile;
import com.vasubhakt.DevAllCPService.Repo.CpProfileRepo;
import com.vasubhakt.DevAllCPService.Service.CPService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CPServiceImpl implements CPService {

    private final CpProfileRepo cpRepo;

    @Override
    public CpProfile getUserProfile(String username) {
        return cpRepo.findByUsername(username)
              .orElseThrow(() -> new RuntimeException("User CP Profile not found"));
    }
    
    @Override
    public void createUserProfile(String username) {
        CpProfile profile = new CpProfile();
        profile.setUsername(username);
        cpRepo.save(profile);
    }

    // For testing
    /*@Override
    public CpProfile updateUserProfile(String username, CpProfile updatedProfile) {
        CpProfile profile = cpRepo.findByUsername(username)
              .orElseThrow(() -> new RuntimeException("User CP Profile not found"));
        if(updatedProfile.getCfProfile()!=null) {
            profile.setCfProfile(updatedProfile.getCfProfile());
        }
        if(updatedProfile.getLcProfile()!=null) {
            profile.setLcProfile(updatedProfile.getLcProfile());
        }
        if(updatedProfile.getCcProfile()!=null) {
            profile.setCcProfile(updatedProfile.getCcProfile());
        }
        if(updatedProfile.getAcProfile()!=null) {
            profile.setAcProfile(updatedProfile.getAcProfile());
        }
        return cpRepo.save(profile);
    }*/

    @Override
    public CpProfile deleteUserProfile(String username) {
        CpProfile profile = cpRepo.findByUsername(username)
              .orElseThrow(() -> new RuntimeException("User CP Profile not found"));
        cpRepo.delete(profile);
        return profile;
    }
    
}
