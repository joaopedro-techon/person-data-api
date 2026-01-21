package com.example.person.external;

import com.example.person.api.dto.BirthCityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "externalPersonBirthCityClient",
        url = "${external.person-api.url}"
)
public interface ExternalPersonBirthCityClient {
    
    @GetMapping("/external-person/{id}/birth-city")
    BirthCityDto getBirthCity(@PathVariable("id") Long id);
}

