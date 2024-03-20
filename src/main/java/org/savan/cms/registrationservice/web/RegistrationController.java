package org.savan.cms.registrationservice.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.savan.cms.registrationservice.domain.service.RegistrationService;
import org.savan.cms.registrationservice.dto.MerchantKeycloakUser;
import org.savan.cms.registrationservice.dto.MerchantUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

}
