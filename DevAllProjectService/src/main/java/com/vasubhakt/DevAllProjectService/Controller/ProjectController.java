package com.vasubhakt.DevAllProjectService.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vasubhakt.DevAllProjectService.Model.ProjectProfile;
import com.vasubhakt.DevAllProjectService.Service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;

    @GetMapping("/get")
    public ResponseEntity<?> getUserProfile(@RequestParam("username") String username) {
        try {
            ProjectProfile profile = projectService.getUserProfile(username);
            return ResponseEntity.ok(profile);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create/{username}")
    public ResponseEntity<?> createUserProfile(@PathVariable String username) {
        try {
            projectService.createUserProfile(username);
            return ResponseEntity.ok("Project Profile created successfully");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUserProfile(@RequestParam("username") String username, @RequestBody ProjectProfile updatedProfile) {
        try {
            ProjectProfile profile = projectService.updateUserProfile(username, updatedProfile);
            return ResponseEntity.ok(profile);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUserProfile(@RequestParam("username") String username) {
        try {
            ProjectProfile profile = projectService.deleteUserProfile(username);
            return ResponseEntity.ok(profile);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
