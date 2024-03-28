package org.savan.cms.registrationservice.web;

import jakarta.validation.Valid;
import org.savan.cms.registrationservice.domain.service.RegistrationService;
import org.savan.cms.registrationservice.dto.MerchantKeycloakUser;
import org.savan.cms.registrationservice.dto.MerchantUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposing registration endpoints.
 * <p>
 * author: savan1508 on 9.3.24.
 */
@RestController
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register/merchant")
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantUser registerUser(@Valid @RequestBody MerchantKeycloakUser user) {
        return registrationService.registerMerchantUser(user);
    }

    // TODO: 28.3.24. add endpoint to register customer to the specific store 

}
