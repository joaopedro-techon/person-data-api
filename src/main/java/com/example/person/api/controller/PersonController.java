package com.example.person.api.controller;

import com.example.person.api.dto.PersonCompleteDto;
import com.example.person.service.PersonService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private final PersonService personService;
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Timer requestTimer;

    public PersonController(PersonService personService, MeterRegistry meterRegistry) {
        this.personService = personService;
        this.requestCounter = Counter.builder("person.endpoint.requests.total")
                .description("Total number of requests to person endpoint")
                .tag("endpoint", "getPersonById")
                .register(meterRegistry);
        this.errorCounter = Counter.builder("person.endpoint.errors.total")
                .description("Total number of errors in person endpoint")
                .tag("endpoint", "getPersonById")
                .register(meterRegistry);
        this.requestTimer = Timer.builder("person.endpoint.response.time")
                .description("Response time for person endpoint")
                .tag("endpoint", "getPersonById")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.75, 0.95, 0.99, 0.999)
                .register(meterRegistry);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonCompleteDto> getPerson(@PathVariable("id") Long id) {
        requestCounter.increment();
        Timer.Sample sample = Timer.start();
        
        try {
            PersonCompleteDto person = personService.getPersonById(id);
            if (person == null) {
                errorCounter.increment();
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(person);
        } catch (Exception e) {
            errorCounter.increment();
            throw e;
        } finally {
            sample.stop(requestTimer);
        }
    }
}
