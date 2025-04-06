package sme.hub.business.services.impl;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sme.hub.business.config.KeycloakConfigProperties;
import sme.hub.business.dto.CreateUsersDto;
import sme.hub.business.dto.CreateUsersRequest;
import sme.hub.business.repo.UsersRepo;
import sme.hub.business.services.UsersService;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepo usersRepo;
    private final Keycloak keycloak;
    private final KeycloakConfigProperties keycloakConfig;

    @Override
    public void register(CreateUsersRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        user.setCredentials(Collections.singletonList(credential));

        try (Response response = keycloak.realm(keycloakConfig.getRealm()).users().create(user)) {
            if (response.getStatus() != 201) {
                throw new IllegalStateException("Failed to create user: " + response.getStatus());
            }

            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            CreateUsersDto usersDto = CreateUsersDto.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .keycloakId(userId)
                    .name(request.getName())
                    .build();

            usersRepo.create(usersDto);
        }
    }

    @Override
    public void logout(String refreshToken) {
        String logoutUrl = keycloakConfig.getServerUrl() + "/realms/" + keycloakConfig.getRealm() + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakConfig.getClientId());
        body.add("client_secret", keycloakConfig.getClientSecret());
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForEntity(logoutUrl, request, String.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to logout: " + e.getMessage(), e);
        }
    }
}
