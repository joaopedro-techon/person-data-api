package com.example.person.service;

import com.example.person.api.dto.*;
import com.example.person.external.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private ExternalPersonClient externalPersonClient;

    @Mock
    private ExternalPersonAddressClient addressClient;

    @Mock
    private ExternalPersonBirthDateClient birthDateClient;

    @Mock
    private ExternalPersonParentsClient parentsClient;

    @Mock
    private ExternalPersonPhoneClient phoneClient;

    @Mock
    private ExternalPersonEducationClient educationClient;

    @Mock
    private ExternalPersonBirthCityClient birthCityClient;

    @Mock
    private Executor externalApiTaskExecutor;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    void setUp() {
        // Configura o executor para executar tarefas sincronamente nos testes
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(externalApiTaskExecutor).execute(any(Runnable.class));
    }

    @Test
    void shouldGetPersonById() {
        // Given
        Long id = 1L;
        
        PersonDto personDto = new PersonDto(id, "João Silva", 30, "joao.silva@example.com");
        AddressDto addressDto = new AddressDto(
                id, "Rua das Flores", "123", "Apto 45", "Centro",
                "São Paulo", "SP", "01310-100", "Brasil"
        );
        BirthDateDto birthDateDto = new BirthDateDto(
                id, LocalDate.of(1993, 5, 15), 30, "Touro"
        );
        ParentsDto parentsDto = new ParentsDto(id, "Carlos Silva", "Maria Silva");
        
        List<PhoneDto.PhoneInfo> phones = Arrays.asList(
                new PhoneDto.PhoneInfo("MOBILE", "11987654321", "+55")
        );
        PhoneDto phoneDto = new PhoneDto(id, phones);
        
        EducationDto educationDto = new EducationDto(
                id, "GRADUATE", "Universidade de São Paulo",
                "Ciência da Computação", 2015, true
        );
        
        BirthCityDto.Coordinates coordinates = new BirthCityDto.Coordinates(-23.5505, -46.6333);
        BirthCityDto birthCityDto = new BirthCityDto(
                id, "São Paulo", "SP", "Brasil", coordinates
        );

        when(externalPersonClient.getExternalPerson(id)).thenReturn(personDto);
        when(addressClient.getAddress(id)).thenReturn(addressDto);
        when(birthDateClient.getBirthDate(id)).thenReturn(birthDateDto);
        when(parentsClient.getParents(id)).thenReturn(parentsDto);
        when(phoneClient.getPhone(id)).thenReturn(phoneDto);
        when(educationClient.getEducation(id)).thenReturn(educationDto);
        when(birthCityClient.getBirthCity(id)).thenReturn(birthCityDto);

        // When
        PersonCompleteDto result = personService.getPersonById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("João Silva", result.getName());
        assertEquals(30, result.getAge());
        assertEquals("joao.silva@example.com", result.getEmail());
        assertNotNull(result.getAddress());
        assertNotNull(result.getBirthDate());
        assertNotNull(result.getParents());
        assertNotNull(result.getPhone());
        assertNotNull(result.getEducation());
        assertNotNull(result.getBirthCity());
        
        // Verifica que todos os clients foram chamados
        verify(externalPersonClient, times(1)).getExternalPerson(id);
        verify(addressClient, times(1)).getAddress(id);
        verify(birthDateClient, times(1)).getBirthDate(id);
        verify(parentsClient, times(1)).getParents(id);
        verify(phoneClient, times(1)).getPhone(id);
        verify(educationClient, times(1)).getEducation(id);
        verify(birthCityClient, times(1)).getBirthCity(id);
    }

    @Test
    void shouldThrowExceptionWhenExternalCallFails() {
        // Given
        Long id = 1L;
        when(externalPersonClient.getExternalPerson(id))
                .thenThrow(new RuntimeException("External API error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> personService.getPersonById(id));
    }
}
