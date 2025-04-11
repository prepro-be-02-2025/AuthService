package sme.hub.business.services;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sme.hub.business.config.KeycloakConfigProperties;

@Service
public class KeycloakPasswordValidator {
    private final WebClient webClient;

    public KeycloakPasswordValidator(WebClient.Builder webClientBuilder, KeycloakConfigProperties config) {
        this.webClient = webClientBuilder
                .baseUrl(config.getServerUrl())
                .build();
    }

    public Mono<Boolean> validateCurrentPassword(String realm, String clientId, String username, String password) {
        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("username", username)
                        .with("password", password))
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorResume(e -> Mono.just(false));
    }
}
