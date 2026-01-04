package com.example.person.external;

import com.example.person.api.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "externalPersonClient",
        url = "${external.person-api.url}"
)
public interface ExternalPersonClient {

    @GetMapping("/external-person/{id}")
    PersonDto getExternalPerson(@PathVariable("id") Long id);
}


