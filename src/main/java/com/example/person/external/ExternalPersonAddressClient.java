package com.example.person.external;

import com.example.person.api.dto.AddressDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "externalPersonAddressClient",
        url = "${external.person-api.url}"
)
public interface ExternalPersonAddressClient {
    
    @GetMapping("/external-person/{id}/address")
    AddressDto getAddress(@PathVariable("id") Long id);
}

