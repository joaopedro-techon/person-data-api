package com.example.person.service;

import com.example.person.api.dto.PersonDto;
import com.example.person.external.ExternalPersonClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final ExternalPersonClient externalPersonClient;
    private final Tracer tracer;


    public PersonService(ExternalPersonClient externalPersonClient) {
        this.externalPersonClient = externalPersonClient;
        this.tracer = GlobalOpenTelemetry.getTracer("person-api-service", "1.0.0");
    }

    /**
     * Bulkhead para limitar o número de chamadas concorrentes ao serviço externo.
     *
     * Configuração no application.properties:
     *
     * resilience4j.bulkhead.instances.externalPersonBulkhead.maxConcurrentCalls=200
     * resilience4j.bulkhead.instances.externalPersonBulkhead.maxWaitDuration=0
     */
    @Bulkhead(name = "externalPersonBulkhead", type = Bulkhead.Type.SEMAPHORE)
    public PersonDto getPersonById(Long id) {
        // Neste exemplo, apenas delegamos à API externa via Feign.
        // Você poderia adicionar regras de negócio aqui se quiser.

        // Cria um span filho para a simulação de latência
        Span latencySpan = tracer.spanBuilder("person-api-service.chamadaServicoExterno")
                .startSpan();

        try (Scope latencyScope = latencySpan.makeCurrent()) {
            return externalPersonClient.getExternalPerson(id);
        } catch (Exception e) {
            latencySpan.recordException(e);
            latencySpan.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, "Interrupted");
            throw new RuntimeException(e);
        } finally {
            latencySpan.end();
        }

    }
}


