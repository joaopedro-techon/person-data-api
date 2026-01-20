package com.example.mockapi.controller;

import com.example.mockapi.dto.*;
import com.example.mockapi.dto.BirthCityResponse.Coordinates;
import com.example.mockapi.dto.PhoneResponse.PhoneInfo;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
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

    private static final int FIXED_LATENCY_MS = 200;
    
    // Tracer obtido do OpenTelemetry Java Agent
    private final Tracer tracer;

    public MockPersonController() {
        // Obtém o Tracer do OpenTelemetry global (inicializado pelo Java Agent)
        this.tracer = GlobalOpenTelemetry.getTracer("mock-api", "1.0.0");
    }

    @GetMapping("/external-person/{id}")
    public PersonResponse getPerson(@PathVariable Long id) {
        // O OpenTelemetry Agent cria automaticamente o span HTTP
        // Aqui criamos spans filhos dentro do contexto existente
        
        // Cria um span filho para a simulação de latência
        Span latencySpan = tracer.spanBuilder("mock-api.simulateLatency")
                .setAttribute("latency.ms", FIXED_LATENCY_MS)
                .startSpan();
        
        try (Scope latencyScope = latencySpan.makeCurrent()) {
            // Simula latência fixa de 500ms
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            latencySpan.addEvent("Latency simulation completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            latencySpan.recordException(e);
            latencySpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Interrupted");
            log.error("Thread interrupted during latency simulation", e);
            throw new RuntimeException("Latency simulation interrupted", e);
        } finally {
            latencySpan.end();
        }
        
        // Cria um span filho para construção da resposta
        Span buildResponseSpan = tracer.spanBuilder("mock-api.buildResponse")
                .setAttribute("response.type", "PersonResponse")
                .startSpan();
        
        PersonResponse response;
        try (Scope buildScope = buildResponseSpan.makeCurrent()) {
            response = new PersonResponse(
                id,
                "João Silva",
                30,
                "joao.silva@example.com"
            );
            
            buildResponseSpan.setAttribute("person.name", response.getName());
            buildResponseSpan.setAttribute("person.age", response.getAge());
            buildResponseSpan.addEvent("Response built successfully");
        } finally {
            buildResponseSpan.end();
        }
        
        return response;
    }

    // 1 - Buscar dados do endereço de um cliente
    @GetMapping("/external-person/{id}/address")
    public AddressResponse getAddress(@PathVariable Long id) {
        // Span para simulação de latência
        Span latencySpan = tracer.spanBuilder("mock-api.simulateLatency")
                .setAttribute("latency.ms", FIXED_LATENCY_MS)
                .setAttribute("endpoint", "address")
                .startSpan();
        
        try (Scope latencyScope = latencySpan.makeCurrent()) {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            latencySpan.addEvent("Latency simulation completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            latencySpan.recordException(e);
            latencySpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Interrupted");
            log.error("Thread interrupted during latency simulation", e);
            throw new RuntimeException("Latency simulation interrupted", e);
        } finally {
            latencySpan.end();
        }
        
        // Span para construção da resposta
        Span buildResponseSpan = tracer.spanBuilder("mock-api.buildResponse")
                .setAttribute("response.type", "AddressResponse")
                .setAttribute("person.id", id)
                .startSpan();
        
        AddressResponse response;
        try (Scope buildScope = buildResponseSpan.makeCurrent()) {
            response = new AddressResponse(
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
            
            buildResponseSpan.setAttribute("address.city", response.getCity());
            buildResponseSpan.setAttribute("address.state", response.getState());
            buildResponseSpan.setAttribute("address.zipCode", response.getZipCode());
            buildResponseSpan.addEvent("Response built successfully");
        } finally {
            buildResponseSpan.end();
        }
        
        return response;
    }

    // 2 - Buscar dados referente a data de nascimento do cliente
    @GetMapping("/external-person/{id}/birth-date")
    public BirthDateResponse getBirthDate(@PathVariable Long id) {
        // Span para simulação de latência
        Span latencySpan = tracer.spanBuilder("mock-api.simulateLatency")
                .setAttribute("latency.ms", FIXED_LATENCY_MS)
                .setAttribute("endpoint", "birth-date")
                .startSpan();
        
        try (Scope latencyScope = latencySpan.makeCurrent()) {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            latencySpan.addEvent("Latency simulation completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            latencySpan.recordException(e);
            latencySpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Interrupted");
            log.error("Thread interrupted during latency simulation", e);
            throw new RuntimeException("Latency simulation interrupted", e);
        } finally {
            latencySpan.end();
        }
        
        // Span para construção da resposta
        Span buildResponseSpan = tracer.spanBuilder("mock-api.buildResponse")
                .setAttribute("response.type", "BirthDateResponse")
                .setAttribute("person.id", id)
                .startSpan();
        
        BirthDateResponse response;
        try (Scope buildScope = buildResponseSpan.makeCurrent()) {
            LocalDate birthDate = LocalDate.of(1993, 5, 15);
            response = new BirthDateResponse(
                id,
                birthDate,
                30,
                "Touro"
            );
            
            buildResponseSpan.setAttribute("birth.date", birthDate.toString());
            buildResponseSpan.setAttribute("person.age", response.getAge());
            buildResponseSpan.setAttribute("zodiac.sign", response.getZodiacSign());
            buildResponseSpan.addEvent("Response built successfully");
        } finally {
            buildResponseSpan.end();
        }
        
        return response;
    }

    // 3 - Buscar nomes do pai e mae do cliente
    @GetMapping("/external-person/{id}/parents")
    public ParentsResponse getParents(@PathVariable Long id) {
        // Span para simulação de latência
        Span latencySpan = tracer.spanBuilder("mock-api.simulateLatency")
                .setAttribute("latency.ms", FIXED_LATENCY_MS)
                .setAttribute("endpoint", "parents")
                .startSpan();
        
        try (Scope latencyScope = latencySpan.makeCurrent()) {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            latencySpan.addEvent("Latency simulation completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            latencySpan.recordException(e);
            latencySpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Interrupted");
            log.error("Thread interrupted during latency simulation", e);
            throw new RuntimeException("Latency simulation interrupted", e);
        } finally {
            latencySpan.end();
        }
        
        // Span para construção da resposta
        Span buildResponseSpan = tracer.spanBuilder("mock-api.buildResponse")
                .setAttribute("response.type", "ParentsResponse")
                .setAttribute("person.id", id)
                .startSpan();
        
        ParentsResponse response;
        try (Scope buildScope = buildResponseSpan.makeCurrent()) {
            response = new ParentsResponse(
                id,
                "Carlos Silva",
                "Maria Silva"
            );
            
            buildResponseSpan.setAttribute("father.name", response.getFatherName());
            buildResponseSpan.setAttribute("mother.name", response.getMotherName());
            buildResponseSpan.addEvent("Response built successfully");
        } finally {
            buildResponseSpan.end();
        }
        
        return response;
    }

    // 4 - Buscar dados de telefone do cliente
    @GetMapping("/external-person/{id}/phone")
    public PhoneResponse getPhone(@PathVariable Long id) {
        // Span para simulação de latência
        Span latencySpan = tracer.spanBuilder("mock-api.simulateLatency")
                .setAttribute("latency.ms", FIXED_LATENCY_MS)
                .setAttribute("endpoint", "phone")
                .startSpan();
        
        try (Scope latencyScope = latencySpan.makeCurrent()) {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            latencySpan.addEvent("Latency simulation completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            latencySpan.recordException(e);
            latencySpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Interrupted");
            log.error("Thread interrupted during latency simulation", e);
            throw new RuntimeException("Latency simulation interrupted", e);
        } finally {
            latencySpan.end();
        }
        
        // Span para construção da resposta
        Span buildResponseSpan = tracer.spanBuilder("mock-api.buildResponse")
                .setAttribute("response.type", "PhoneResponse")
                .setAttribute("person.id", id)
                .startSpan();
        
        PhoneResponse response;
        try (Scope buildScope = buildResponseSpan.makeCurrent()) {
            List<PhoneInfo> phones = Arrays.asList(
                new PhoneInfo("MOBILE", "11987654321", "+55"),
                new PhoneInfo("HOME", "1133334444", "+55"),
                new PhoneInfo("WORK", "1122223333", "+55")
            );
            
            response = new PhoneResponse(id, phones);
            
            buildResponseSpan.setAttribute("phone.count", phones.size());
            buildResponseSpan.addEvent("Response built successfully");
        } finally {
            buildResponseSpan.end();
        }
        
        return response;
    }

    // 5 - Buscar dados de escolaridade do cliente
    @GetMapping("/external-person/{id}/education")
    public EducationResponse getEducation(@PathVariable Long id) {
        // Span para simulação de latência
        Span latencySpan = tracer.spanBuilder("mock-api.simulateLatency")
                .setAttribute("latency.ms", FIXED_LATENCY_MS)
                .setAttribute("endpoint", "education")
                .startSpan();
        
        try (Scope latencyScope = latencySpan.makeCurrent()) {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            latencySpan.addEvent("Latency simulation completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            latencySpan.recordException(e);
            latencySpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Interrupted");
            log.error("Thread interrupted during latency simulation", e);
            throw new RuntimeException("Latency simulation interrupted", e);
        } finally {
            latencySpan.end();
        }
        
        // Span para construção da resposta
        Span buildResponseSpan = tracer.spanBuilder("mock-api.buildResponse")
                .setAttribute("response.type", "EducationResponse")
                .setAttribute("person.id", id)
                .startSpan();
        
        EducationResponse response;
        try (Scope buildScope = buildResponseSpan.makeCurrent()) {
            response = new EducationResponse(
                id,
                "GRADUATE",
                "Universidade de São Paulo",
                "Ciência da Computação",
                2015,
                true
            );
            
            buildResponseSpan.setAttribute("education.level", response.getEducationLevel());
            buildResponseSpan.setAttribute("education.institution", response.getInstitution());
            buildResponseSpan.setAttribute("education.course", response.getCourse());
            buildResponseSpan.setAttribute("education.graduationYear", response.getGraduationYear());
            buildResponseSpan.setAttribute("education.isCompleted", response.getIsCompleted());
            buildResponseSpan.addEvent("Response built successfully");
        } finally {
            buildResponseSpan.end();
        }
        
        return response;
    }

    // 6 - Buscar cidade de nascimento do cliente
    @GetMapping("/external-person/{id}/birth-city")
    public BirthCityResponse getBirthCity(@PathVariable Long id) {
        // Span para simulação de latência
        Span latencySpan = tracer.spanBuilder("mock-api.simulateLatency")
                .setAttribute("latency.ms", FIXED_LATENCY_MS)
                .setAttribute("endpoint", "birth-city")
                .startSpan();
        
        try (Scope latencyScope = latencySpan.makeCurrent()) {
            TimeUnit.MILLISECONDS.sleep(FIXED_LATENCY_MS);
            latencySpan.addEvent("Latency simulation completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            latencySpan.recordException(e);
            latencySpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Interrupted");
            log.error("Thread interrupted during latency simulation", e);
            throw new RuntimeException("Latency simulation interrupted", e);
        } finally {
            latencySpan.end();
        }
        
        // Span para construção da resposta
        Span buildResponseSpan = tracer.spanBuilder("mock-api.buildResponse")
                .setAttribute("response.type", "BirthCityResponse")
                .setAttribute("person.id", id)
                .startSpan();
        
        BirthCityResponse response;
        try (Scope buildScope = buildResponseSpan.makeCurrent()) {
            Coordinates coordinates = new Coordinates(-23.5505, -46.6333);
            response = new BirthCityResponse(
                id,
                "São Paulo",
                "SP",
                "Brasil",
                coordinates
            );
            
            buildResponseSpan.setAttribute("birth.city", response.getBirthCity());
            buildResponseSpan.setAttribute("birth.state", response.getBirthState());
            buildResponseSpan.setAttribute("birth.country", response.getBirthCountry());
            buildResponseSpan.setAttribute("coordinates.latitude", coordinates.getLatitude());
            buildResponseSpan.setAttribute("coordinates.longitude", coordinates.getLongitude());
            buildResponseSpan.addEvent("Response built successfully");
        } finally {
            buildResponseSpan.end();
        }
        
        return response;
    }
}

