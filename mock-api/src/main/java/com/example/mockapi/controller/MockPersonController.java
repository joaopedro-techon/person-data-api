package com.example.mockapi.controller;

import com.example.mockapi.dto.PersonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class MockPersonController {

    private static final int FIXED_LATENCY_MS = 200;

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
}

