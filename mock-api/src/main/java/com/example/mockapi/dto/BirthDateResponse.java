package com.example.mockapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BirthDateResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("birthDate")
    private LocalDate birthDate;
    
    @JsonProperty("age")
    private Integer age;
    
    @JsonProperty("zodiacSign")
    private String zodiacSign;
}

