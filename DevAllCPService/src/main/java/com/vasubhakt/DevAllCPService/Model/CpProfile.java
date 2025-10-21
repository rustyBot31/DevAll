package com.vasubhakt.DevAllCPService.Model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Document(collection = "cp_profiles")
@AllArgsConstructor
@RequiredArgsConstructor

public class CpProfile {
    
    @Id
    private ObjectId id;

    private CFProfile cfPorfile; // Codeforces Profile
    private LCProfile lcProfile; // Leetcode Profile
    private CCProfile ccProfile; // Codechef Profile

}
