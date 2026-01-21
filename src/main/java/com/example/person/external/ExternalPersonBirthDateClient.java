package com.example.person.external;

import com.example.person.api.dto.BirthDateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "externalPersonBirthDateClient",
        url = "${external.person-api.url}"
)
public interface ExternalPersonBirthDateClient {
    
    @GetMapping("/external-person/{id}/birth-date")
    BirthDateDto getBirthDate(@PathVariable("id") Long id);
}

