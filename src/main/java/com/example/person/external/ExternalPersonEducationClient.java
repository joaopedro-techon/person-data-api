package com.example.person.external;

import com.example.person.api.dto.EducationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "externalPersonEducationClient",
        url = "${external.person-api.url}"
)
public interface ExternalPersonEducationClient {
    
    @GetMapping("/external-person/{id}/education")
    EducationDto getEducation(@PathVariable("id") Long id);
}

