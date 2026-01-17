package com.example.mockapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("phones")
    private List<PhoneInfo> phones;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneInfo {
        @JsonProperty("type")
        private String type; // MOBILE, HOME, WORK
        
        @JsonProperty("number")
        private String number;
        
        @JsonProperty("countryCode")
        private String countryCode;
    }
}

