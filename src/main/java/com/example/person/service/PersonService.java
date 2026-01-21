package com.example.person.service;

import com.example.person.api.dto.*;
import com.example.person.external.*;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Serviço que busca dados completos de uma pessoa fazendo chamadas assíncronas
 * para todos os endpoints do mock-api e consolidando as respostas.
 */
@Slf4j
@Service
public class PersonService {

    private final ExternalPersonClient externalPersonClient;
    private final ExternalPersonAddressClient addressClient;
    private final ExternalPersonBirthDateClient birthDateClient;
    private final ExternalPersonParentsClient parentsClient;
    private final ExternalPersonPhoneClient phoneClient;
    private final ExternalPersonEducationClient educationClient;
    private final ExternalPersonBirthCityClient birthCityClient;
    private final Executor externalApiTaskExecutor;
    private final Tracer tracer;

    public PersonService(
            ExternalPersonClient externalPersonClient,
            ExternalPersonAddressClient addressClient,
            ExternalPersonBirthDateClient birthDateClient,
            ExternalPersonParentsClient parentsClient,
            ExternalPersonPhoneClient phoneClient,
            ExternalPersonEducationClient educationClient,
            ExternalPersonBirthCityClient birthCityClient,
            @Qualifier("externalApiTaskExecutor") Executor externalApiTaskExecutor) {
        this.externalPersonClient = externalPersonClient;
        this.addressClient = addressClient;
        this.birthDateClient = birthDateClient;
        this.parentsClient = parentsClient;
        this.phoneClient = phoneClient;
        this.educationClient = educationClient;
        this.birthCityClient = birthCityClient;
        this.externalApiTaskExecutor = externalApiTaskExecutor;
        this.tracer = GlobalOpenTelemetry.getTracer("person-api-service", "1.0.0");
    }

    /**
     * Busca dados completos de uma pessoa fazendo chamadas assíncronas
     * para todos os endpoints do mock-api e consolidando as respostas.
     *
     * Bulkhead para limitar o número de chamadas concorrentes ao serviço externo.
     *
     * Configuração no application.properties:
     * resilience4j.bulkhead.instances.externalPersonBulkhead.maxConcurrentCalls=200
     * resilience4j.bulkhead.instances.externalPersonBulkhead.maxWaitDuration=0
     */
    @Bulkhead(name = "externalPersonBulkhead", type = io.github.resilience4j.bulkhead.annotation.Bulkhead.Type.SEMAPHORE)
    public PersonCompleteDto getPersonById(Long id) {
        Span serviceSpan = tracer.spanBuilder("person-api-service.getPersonComplete")
                .setAttribute("person.id", id)
                .startSpan();

        try (Scope scope = serviceSpan.makeCurrent()) {
            log.debug("Starting async calls for person id={}", id);

            // Cria todas as chamadas assíncronas em paralelo
            CompletableFuture<PersonDto> personFuture = callAsync(
                    () -> externalPersonClient.getExternalPerson(id),
                    "getPerson"
            );

            CompletableFuture<AddressDto> addressFuture = callAsync(
                    () -> addressClient.getAddress(id),
                    "getAddress"
            );

            CompletableFuture<BirthDateDto> birthDateFuture = callAsync(
                    () -> birthDateClient.getBirthDate(id),
                    "getBirthDate"
            );

            CompletableFuture<ParentsDto> parentsFuture = callAsync(
                    () -> parentsClient.getParents(id),
                    "getParents"
            );

            CompletableFuture<PhoneDto> phoneFuture = callAsync(
                    () -> phoneClient.getPhone(id),
                    "getPhone"
            );

            CompletableFuture<EducationDto> educationFuture = callAsync(
                    () -> educationClient.getEducation(id),
                    "getEducation"
            );

            CompletableFuture<BirthCityDto> birthCityFuture = callAsync(
                    () -> birthCityClient.getBirthCity(id),
                    "getBirthCity"
            );

            // Aguarda todas as chamadas completarem e faz o merge
            serviceSpan.addEvent("Waiting for all async calls to complete");

            PersonCompleteDto result = CompletableFuture.allOf(
                    personFuture, addressFuture, birthDateFuture, parentsFuture,
                    phoneFuture, educationFuture, birthCityFuture
            ).thenApply(v -> {
                try {
                    // Faz o merge de todas as respostas
                    PersonDto person = personFuture.get();
                    AddressDto address = addressFuture.get();
                    BirthDateDto birthDate = birthDateFuture.get();
                    ParentsDto parents = parentsFuture.get();
                    PhoneDto phone = phoneFuture.get();
                    EducationDto education = educationFuture.get();
                    BirthCityDto birthCity = birthCityFuture.get();

                    serviceSpan.addEvent("All responses received, merging data");

                    return PersonCompleteDto.builder()
                            .id(person.getId())
                            .name(person.getName())
                            .age(person.getAge())
                            .email(person.getEmail())
                            .address(address)
                            .birthDate(birthDate)
                            .parents(parents)
                            .phone(phone)
                            .education(education)
                            .birthCity(birthCity)
                            .build();
                } catch (Exception e) {
                    log.error("Error merging responses for person id={}", id, e);
                    serviceSpan.recordException(e);
                    serviceSpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Error merging responses");
                    throw new RuntimeException("Error merging responses", e);
                }
            }).join(); // join() bloqueia até completar, mas todas as chamadas já estão em paralelo

            serviceSpan.addEvent("Person data merged successfully");
            log.debug("Successfully retrieved complete person data for id={}", id);

            return result;

        } catch (Exception e) {
            log.error("Error retrieving person data for id={}", id, e);
            serviceSpan.recordException(e);
            serviceSpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Error retrieving person data");
            throw new RuntimeException("Error retrieving person data", e);
        } finally {
            serviceSpan.end();
        }
    }

    /**
     * Executa uma chamada assíncrona ao mock-api com tracing.
     */
    private <T> CompletableFuture<T> callAsync(java.util.function.Supplier<T> supplier, String operationName) {
        return CompletableFuture.supplyAsync(() -> {
            Span span = tracer.spanBuilder("person-api-service.externalCall")
                    .setAttribute("operation", operationName)
                    .startSpan();

            try (Scope scope = span.makeCurrent()) {
                span.addEvent("Starting external API call");
                T result = supplier.get();
                span.addEvent("External API call completed successfully");
                span.setStatus(io.opentelemetry.api.trace.StatusCode.OK);
                return result;
            } catch (Exception e) {
                log.error("Error in async call for operation={}", operationName, e);
                span.recordException(e);
                span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "External API call failed");
                throw new RuntimeException("External API call failed for " + operationName, e);
            } finally {
                span.end();
            }
        }, externalApiTaskExecutor);
    }
}
