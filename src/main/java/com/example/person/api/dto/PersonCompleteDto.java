package com.example.person.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO consolidado que agrupa todas as informações de uma pessoa
 * obtidas de múltiplos endpoints do mock-api
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonCompleteDto {
    
    // Dados básicos
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("age")
    private Integer age;
    
    @JsonProperty("email")
    private String email;
    
    // Endereço
    @JsonProperty("address")
    private AddressDto address;
    
    // Data de nascimento
    @JsonProperty("birthDate")
    private BirthDateDto birthDate;
    
    // Pais
    @JsonProperty("parents")
    private ParentsDto parents;
    
    // Telefones
    @JsonProperty("phone")
    private PhoneDto phone;
    
    // Escolaridade
    @JsonProperty("education")
    private EducationDto education;
    
    // Cidade de nascimento
    @JsonProperty("birthCity")
    private BirthCityDto birthCity;
}

