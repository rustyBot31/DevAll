package com.vasubhakt.DevAllCPService.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPFetchRequest {
    private String username;
    private String platform;
    private String handle;
}
