package org.savan.cms.registrationservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.savan.cms.registrationservice.dto.MerchantKeycloakUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for registration process.
 * <p>
 * author: savan1508 on 24.3.24.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RegistrationControllerTestIT {

    // keycloak oauth client data
    private static final String CLIENT_ID = "registration-service";
    private static final String CLIENT_SECRET = "password";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    @Autowired
    private WebClient webClient;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile("keycloak/realm-export.json");

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/cms");
        registry.add("keycloak.admin.baseUrl",
                () -> keycloak.getAuthServerUrl() + "/admin/realms/cms/users");

    }

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void testRegisterUser_shouldRegisterUserInKeyCloak() throws Exception {

        MerchantKeycloakUser keycloakRequestBody = createKeycloakRequestBody();

        mockMvc.perform(post("/register/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(keycloakRequestBody))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated());


        String newlyRegisteredUserAsString = webClient.get()
                .uri("http://localhost:{port}/admin/realms/cms/users", keycloak.getHttpPort())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        assertAll(
                () -> assertNotNull(newlyRegisteredUserAsString),
                () -> assertTrue(newlyRegisteredUserAsString.contains(keycloakRequestBody.username())),
                () -> assertTrue(newlyRegisteredUserAsString.contains(keycloakRequestBody.email())),
                () -> assertTrue(newlyRegisteredUserAsString.contains(keycloakRequestBody.firstName())),
                () -> assertTrue(newlyRegisteredUserAsString.contains(keycloakRequestBody.lastName()))
        );


    }


    private MerchantKeycloakUser createKeycloakRequestBody() {
        return new MerchantKeycloakUser("testusername", "testPassword", "testemail@gmail.com", "testFirstName", "testLastName");
    }


}
