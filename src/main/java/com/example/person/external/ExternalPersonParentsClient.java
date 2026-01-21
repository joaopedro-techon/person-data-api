package com.example.person.external;

import com.example.person.api.dto.ParentsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "externalPersonParentsClient",
        url = "${external.person-api.url}"
)
public interface ExternalPersonParentsClient {
    
    @GetMapping("/external-person/{id}/parents")
    ParentsDto getParents(@PathVariable("id") Long id);
}

