package com.example.mockapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentsResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("fatherName")
    private String fatherName;
    
    @JsonProperty("motherName")
    private String motherName;
}

