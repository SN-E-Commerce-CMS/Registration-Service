package org.savan.cms.registrationservice.domain.expection;

/**
 * Exception class when user cannot be registered into Keycloak.
 * <p>
 * author: savan1508 on 13.3.24.
 */
public class KeycloakRegistrationException extends RuntimeException {

    public KeycloakRegistrationException(String message) {
        super(message);
    }
}
