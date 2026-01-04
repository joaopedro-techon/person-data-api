package com.example.person.service;

import com.example.person.api.dto.PersonDto;
import com.example.person.external.ExternalPersonClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private ExternalPersonClient externalPersonClient;

    @InjectMocks
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
    void shouldGetPersonById() {
        // Given
        PersonDto expectedPerson = new PersonDto(1L, "João Silva", 30, "joao.silva@example.com");
        when(externalPersonClient.getExternalPerson(1L)).thenReturn(expectedPerson);

        // When
        PersonDto result = personService.getPersonById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getName());
        assertEquals(30, result.getAge());
        assertEquals("joao.silva@example.com", result.getEmail());
    }

    @Test
    void shouldReturnNullWhenPersonNotFound() {
        // Given
        when(externalPersonClient.getExternalPerson(999L)).thenReturn(null);

        // When
        PersonDto result = personService.getPersonById(999L);

        // Then
        assertNull(result);
    }
}

