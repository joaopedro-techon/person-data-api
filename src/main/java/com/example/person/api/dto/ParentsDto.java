package com.example.person.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentsDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("fatherName")
    private String fatherName;
    
    @JsonProperty("motherName")
    private String motherName;
}

