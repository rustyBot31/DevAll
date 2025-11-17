package com.vasubhakt.DevAllCPService.Model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Document(collection = "cp_profiles")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class CpProfile {
    
    @Id
    private ObjectId id;

    @NonNull
    @Indexed(unique = true)
    private String username;

    private CFProfile cfProfile; // Codeforces Profile
    private LCProfile lcProfile; // Leetcode Profile
    private CCProfile ccProfile; // Codechef Profile
    private ACProfile acProfile; // Atcoder Profile

}
