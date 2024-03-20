package org.savan.cms.registrationservice.domain.expection;

/**
 * Exception class when Keycloak confirmation email cannot be sent.
 * <p>
 * author: savan1508 on 13.3.24.
 */
public class KeycloakEmailException extends RuntimeException {

    public KeycloakEmailException(String message) {
        super(message);
    }
}
