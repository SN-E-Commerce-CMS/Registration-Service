package org.savan.cms.registrationservice.proxy;

import org.savan.cms.registrationservice.domain.expection.KeycloakEmailException;
import org.savan.cms.registrationservice.domain.expection.KeycloakRegistrationException;
import org.savan.cms.registrationservice.dto.MerchantKeycloakUser;
import org.savan.cms.registrationservice.dto.MerchantUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proxy class which serves to communicate with Keycloak.
 * <p>
 *     <ul>
 *         <li>user registration request.</li>
 *         <li>sending confirmation email.</li>
 *     </ul>
 * </p>
 * <p>
 * author: savan1508 on 9.3.24.
 */
@Component
public class KeycloakRegistrationProxy {
    private final Logger LOGGER = LoggerFactory.getLogger(KeycloakRegistrationProxy.class);
    private final WebClient webClient;

    @Value("${keycloak.admin.baseUrl}")
    private String KEYCLOAK_ADMIN_URL;

    public KeycloakRegistrationProxy(WebClient webClient) {
        this.webClient = webClient;
    }


    /**
     * Registers user into keycloak and returns user UUID.
     *
     * @param user {@link MerchantKeycloakUser}
     * @return {@link MerchantUser}.
     */
    public MerchantUser registerKeycloakUser(MerchantKeycloakUser user) {
        return webClient.post()
                .uri(KEYCLOAK_ADMIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createKeycloakRequestBody(user))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        String userUUID = extractUserUuidFromResponse(response);
                        return Mono.just(new MerchantUser(userUUID, user.username(), user.firstName(), user.lastName(), user.email()));
                    } else {
                        LOGGER.error("Keycloak could not register the user: {}:{}", user.username(), user.email());
                        return Mono.error(new KeycloakRegistrationException("Registration failed, please try again later."));
                    }
                })
                .timeout(Duration.ofSeconds(3), Mono.error(new KeycloakRegistrationException("Keycloak is not responding")))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .block();
    }

    /**
     * Sends confirmation mail to user.
     *
     * @param uuid - user uuid.
     */
    public Mono<Void> sendConfirmationEmail(String uuid) {
        return webClient.put()
                .uri(KEYCLOAK_ADMIN_URL + "/{uuid}/send-verify-email", uuid)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(success -> LOGGER.info("e-mail has been sent"))
                .timeout(Duration.ofSeconds(3), Mono.error(new KeycloakEmailException("E-mail could not been sent.")))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)));
    }

    /**
     * Extract user UUID from header.
     *
     * @param response -> keycloak successful response.
     * @return newly registered user UUID.
     */
    private String extractUserUuidFromResponse(ClientResponse response) {
        LOGGER.debug("Extracting user uuid from location header...");
        LOGGER.debug("Response: {}", response);
        return response.headers().header("Location").get(0).split("users/")[1];
    }


    /**
     * Creates request body for keycloak registration.
     *
     * @param user - {@link MerchantKeycloakUser}.
     * @return - Map with registration details.
     */
    private Map<String, Object> createKeycloakRequestBody(MerchantKeycloakUser user) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", user.username());
        requestBody.put("email", user.email());
        requestBody.put("firstName", user.firstName());
        requestBody.put("lastName", user.lastName());
        requestBody.put("enabled", true);
        requestBody.put("emailVerified", false);
//        requestBody.put("requiredActions", List.of("VERIFY_EMAIL", "CONFIGURE_TOTP"));
        requestBody.put("requiredActions", List.of("VERIFY_EMAIL"));
        requestBody.put("credentials", Collections.singletonList(Map.of(
                "type", "password",
                "value", user.password(),
                "temporary", false
        )));
        return requestBody;
    }


}

