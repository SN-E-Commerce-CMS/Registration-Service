package org.savan.cms.registrationservice.domain.service;

import org.savan.cms.registrationservice.domain.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final KeycloakRegistrationProxy keycloakRegistrationProxy;

    public RegistrationService(UserRepository userRepository, KeycloakRegistrationProxy keycloakRegistrationProxy) {
        this.userRepository = userRepository;
        this.keycloakRegistrationProxy = keycloakRegistrationProxy;
    }

    /**
     * Registers user in keycloak db and application db, also sends confirmation mail.
     * @param user {@link MerchantKeycloakUser}
     * @return {@link MerchantUser}
     */
    public MerchantUser registerMerchantUser(MerchantKeycloakUser user) {
        // register user at keycloak
        MerchantUser merchantUser = keycloakRegistrationProxy.registerKeycloakUser(user);

        sendConfirmationMail(merchantUser);

        // save user in db as inactive
        // when user confirm and set 2FA enable it update
        return null;
    }

    private void sendConfirmationMail(MerchantUser merchantUser) {
        keycloakRegistrationProxy.sendConfirmationEmail(merchantUser.uuid()).subscribe(
                success -> LOGGER.info("e-mail has been sent"),
                error -> LOGGER.error("Could not sent an email for: {}", merchantUser.email())
        );
    }
}




