package com.example.person.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("educationLevel")
    private String educationLevel; // ELEMENTARY, HIGH_SCHOOL, UNDERGRADUATE, GRADUATE, POSTGRADUATE
    
    @JsonProperty("institution")
    private String institution;
    
    @JsonProperty("course")
    private String course;
    
    @JsonProperty("graduationYear")
    private Integer graduationYear;
    
    @JsonProperty("isCompleted")
    private Boolean isCompleted;
}

