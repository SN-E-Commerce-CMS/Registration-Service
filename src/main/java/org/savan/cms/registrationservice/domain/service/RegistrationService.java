package org.savan.cms.registrationservice.domain.service;

import org.savan.cms.registrationservice.dto.MerchantKeycloakUser;
import org.savan.cms.registrationservice.dto.MerchantUser;
import org.savan.cms.registrationservice.proxy.KeycloakRegistrationProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Purpose of this service is to handle user registration.
 * <p>
 * author: savan1508 on 9.3.24.
 */
@Service
public class RegistrationService {
    private final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class);
    private final KeycloakRegistrationProxy keycloakRegistrationProxy;

    public RegistrationService(KeycloakRegistrationProxy keycloakRegistrationProxy) {
        this.keycloakRegistrationProxy = keycloakRegistrationProxy;
    }

    /**
     * Registers user in keycloak db and application db, also sends confirmation mail.
     *
     * @param user {@link MerchantKeycloakUser}
     * @return {@link MerchantUser}
     */
    public MerchantUser registerMerchantUser(MerchantKeycloakUser user) {
        MerchantUser merchantUser = keycloakRegistrationProxy.registerKeycloakUser(user);

        sendConfirmationMail(merchantUser);

        return merchantUser;
    }

    private void sendConfirmationMail(MerchantUser merchantUser) {
        keycloakRegistrationProxy.sendConfirmationEmail(merchantUser.uuid()).subscribe(
                success -> LOGGER.info("e-mail has been sent"),
                error -> LOGGER.error("Could not sent an email for: {}", merchantUser.email())
        );
    }
}




