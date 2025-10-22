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
    public CpProfile getUserProfile(String id) {
        return cpRepo.findByUserId(id)
              .orElseThrow(() -> new RuntimeException("User CP Profile not found"));
    }
    
    @Override
    public CpProfile updateUserProfile(String id) {
        CpProfile profile = cpRepo.findByUserId(id)
              .orElseThrow(() -> new RuntimeException("User CP Profile not found"));

        return cpRepo.save(profile);
    }
    
}
