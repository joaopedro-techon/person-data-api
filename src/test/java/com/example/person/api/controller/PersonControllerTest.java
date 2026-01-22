package com.example.person.api.controller;

import com.example.person.api.dto.*;
import com.example.person.service.PersonService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
@Import(PersonControllerTest.TestConfig.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    private WireMockServer wireMockServer;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SimpleMeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8088);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8088);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldReturnPersonWhenExists() throws Exception {
        // Given
        PersonCompleteDto personComplete = createCompletePersonDto(1L);
        when(personService.getPersonById(1L)).thenReturn(personComplete);

        // When & Then
        mockMvc.perform(get("/api/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("joao.silva@example.com"))
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.birthDate").exists())
                .andExpect(jsonPath("$.parents").exists())
                .andExpect(jsonPath("$.phone").exists())
                .andExpect(jsonPath("$.education").exists())
                .andExpect(jsonPath("$.birthCity").exists());
    }

    @Test
    void shouldReturnNotFoundWhenPersonDoesNotExist() throws Exception {
        // Given
        when(personService.getPersonById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/person/999"))
                .andExpect(status().isNotFound());
    }

    private PersonCompleteDto createCompletePersonDto(Long id) {
        AddressDto address = new AddressDto(
                id, "Rua das Flores", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310-100", "Brasil"
        );

        BirthDateDto birthDate = new BirthDateDto(
                id, LocalDate.of(1993, 5, 15), 30, "Touro"
        );

        ParentsDto parents = new ParentsDto(id, "Carlos Silva", "Maria Silva");

        List<PhoneDto.PhoneInfo> phones = Arrays.asList(
                new PhoneDto.PhoneInfo("MOBILE", "11987654321", "+55"),
                new PhoneDto.PhoneInfo("HOME", "1133334444", "+55")
        );
        PhoneDto phone = new PhoneDto(id, phones);

        EducationDto education = new EducationDto(
                id, "GRADUATE", "Universidade de São Paulo",
                "Ciência da Computação", 2015, true
        );

        BirthCityDto.Coordinates coordinates = new BirthCityDto.Coordinates(-23.5505, -46.6333);
        BirthCityDto birthCity = new BirthCityDto(
                id, "São Paulo", "SP", "Brasil", coordinates
        );

        return PersonCompleteDto.builder()
                .id(id)
                .name("João Silva")
                .age(30)
                .email("joao.silva@example.com")
                .address(address)
                .birthDate(birthDate)
                .parents(parents)
                .phone(phone)
                .education(education)
                .birthCity(birthCity)
                .build();
    }
}

