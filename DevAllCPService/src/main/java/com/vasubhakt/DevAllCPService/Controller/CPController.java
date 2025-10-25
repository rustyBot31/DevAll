package com.vasubhakt.DevAllCPService.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vasubhakt.DevAllCPService.Model.CpProfile;
import com.vasubhakt.DevAllCPService.Service.CPService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cp")
@RequiredArgsConstructor
public class CPController {
    
    private final CPService cpService;

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        try {
            CpProfile profile = cpService.getUserProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create/{username}")
    public ResponseEntity<?> createUserProfile(@PathVariable String username) {
        try {
            createUserProfile(username);
            return ResponseEntity.ok("CP Profile created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String userId, @RequestBody CpProfile updatedProfile) {
        try {
            CpProfile profile = cpService.updateUserProfile(userId, updatedProfile);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } 

    @DeleteMapping("/delete/{userId}") 
    public ResponseEntity<?> deleteUserProfile(@PathVariable String userId) {
        try {
            CpProfile profile = cpService.deleteUserProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
