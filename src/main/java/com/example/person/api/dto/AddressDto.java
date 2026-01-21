package com.example.person.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("street")
    private String street;
    
    @JsonProperty("number")
    private String number;
    
    @JsonProperty("complement")
    private String complement;
    
    @JsonProperty("neighborhood")
    private String neighborhood;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("zipCode")
    private String zipCode;
    
    @JsonProperty("country")
    private String country;
}

