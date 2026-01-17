package com.example.mockapi.controller;

import com.example.mockapi.dto.*;
import com.example.mockapi.dto.BirthCityResponse.Coordinates;
import com.example.mockapi.dto.PhoneResponse.PhoneInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class MockPersonController {

    private static final int FIXED_LATENCY_MS = 500;

    @GetMapping("/external-person/{id}")
    public PersonResponse getPerson(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Simula latência fixa de 500ms
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            
            PersonResponse response = new PersonResponse(
                id,
                "João Silva",
                30,
                "joao.silva@example.com"
            );
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Request processed for id={} in {}ms", id, duration);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while processing request for id={}", id, e);
            throw new RuntimeException("Request processing interrupted", e);
        }
    }

    // 1 - Buscar dados do endereço de um cliente
    @GetMapping("/external-person/{id}/address")
    public AddressResponse getAddress(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            
            AddressResponse response = new AddressResponse(
                id,
                "Rua das Flores",
                "123",
                "Apto 45",
                "Centro",
                "São Paulo",
                "SP",
                "01310-100",
                "Brasil"
            );
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Address request processed for id={} in {}ms", id, duration);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while processing address request for id={}", id, e);
            throw new RuntimeException("Request processing interrupted", e);
        }
    }

    // 2 - Buscar dados referente a data de nascimento do cliente
    @GetMapping("/external-person/{id}/birth-date")
    public BirthDateResponse getBirthDate(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            
            LocalDate birthDate = LocalDate.of(1993, 5, 15);
            BirthDateResponse response = new BirthDateResponse(
                id,
                birthDate,
                30,
                "Touro"
            );
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Birth date request processed for id={} in {}ms", id, duration);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while processing birth date request for id={}", id, e);
            throw new RuntimeException("Request processing interrupted", e);
        }
    }

    // 3 - Buscar nomes do pai e mae do cliente
    @GetMapping("/external-person/{id}/parents")
    public ParentsResponse getParents(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            
            ParentsResponse response = new ParentsResponse(
                id,
                "Carlos Silva",
                "Maria Silva"
            );
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Parents request processed for id={} in {}ms", id, duration);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while processing parents request for id={}", id, e);
            throw new RuntimeException("Request processing interrupted", e);
        }
    }

    // 4 - Buscar dados de telefone do cliente
    @GetMapping("/external-person/{id}/phone")
    public PhoneResponse getPhone(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            
            List<PhoneInfo> phones = Arrays.asList(
                new PhoneInfo("MOBILE", "11987654321", "+55"),
                new PhoneInfo("HOME", "1133334444", "+55"),
                new PhoneInfo("WORK", "1122223333", "+55")
            );
            
            PhoneResponse response = new PhoneResponse(id, phones);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Phone request processed for id={} in {}ms", id, duration);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while processing phone request for id={}", id, e);
            throw new RuntimeException("Request processing interrupted", e);
        }
    }

    // 5 - Buscar dados de escolaridade do cliente
    @GetMapping("/external-person/{id}/education")
    public EducationResponse getEducation(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            
            EducationResponse response = new EducationResponse(
                id,
                "GRADUATE",
                "Universidade de São Paulo",
                "Ciência da Computação",
                2015,
                true
            );
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Education request processed for id={} in {}ms", id, duration);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while processing education request for id={}", id, e);
            throw new RuntimeException("Request processing interrupted", e);
        }
    }

    // 6 - Buscar cidade de nascimento do cliente
    @GetMapping("/external-person/{id}/birth-city")
    public BirthCityResponse getBirthCity(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            
            Coordinates coordinates = new Coordinates(-23.5505, -46.6333);
            BirthCityResponse response = new BirthCityResponse(
                id,
                "São Paulo",
                "SP",
                "Brasil",
                coordinates
            );
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Birth city request processed for id={} in {}ms", id, duration);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while processing birth city request for id={}", id, e);
            throw new RuntimeException("Request processing interrupted", e);
        }
    }
}

