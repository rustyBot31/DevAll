package com.vasubhakt.DevAllPortfolioService.Model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "portfolios")
public class Portfolio {
    @Id
    private ObjectId id;
    private String name;
    private String username;
    private String summary;
    private List<String> languages;
    private List<String> frameworks;
    private List<String> tools;
    private List<String> databases;
    private List<String> operatingSystems;
    private List<String> csFundamentals;
    private List<Project> projects;
    private String resumeLink;
    private String linkedInLink;
    private String stackOverflowLink; 

}
