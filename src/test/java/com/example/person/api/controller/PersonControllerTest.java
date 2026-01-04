package com.example.person.api.controller;

import com.example.person.api.dto.PersonDto;
import com.example.person.service.PersonService;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldReturnPersonWhenExists() throws Exception {
        // Given
        PersonDto personDto = new PersonDto(1L, "João Silva", 30, "joao.silva@example.com");
        when(personService.getPersonById(1L)).thenReturn(personDto);

        // When & Then
        mockMvc.perform(get("/api/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.email").value("joao.silva@example.com"));
    }

    @Test
    void shouldReturnNotFoundWhenPersonDoesNotExist() throws Exception {
        // Given
        when(personService.getPersonById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/person/999"))
                .andExpect(status().isNotFound());
    }
}

