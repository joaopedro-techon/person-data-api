package com.example.person.service;

import com.example.person.api.dto.PersonDto;
import com.example.person.external.ExternalPersonClient;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final ExternalPersonClient externalPersonClient;

    public PersonService(ExternalPersonClient externalPersonClient) {
        this.externalPersonClient = externalPersonClient;
    }

    public PersonDto getPersonById(Long id) {
        // Neste exemplo, apenas delegamos à API externa via Feign.
        // Você poderia adicionar regras de negócio aqui se quiser.
        return externalPersonClient.getExternalPerson(id);
    }
}


