package com.example.person.external;

import com.example.person.api.dto.PhoneDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "externalPersonPhoneClient",
        url = "${external.person-api.url}"
)
public interface ExternalPersonPhoneClient {
    
    @GetMapping("/external-person/{id}/phone")
    PhoneDto getPhone(@PathVariable("id") Long id);
}

