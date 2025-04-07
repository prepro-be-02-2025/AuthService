package sme.hub.business.services.impl;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sme.hub.business.config.KeycloakConfigProperties;
import sme.hub.business.dto.CreateUsersDto;
import sme.hub.business.dto.CreateUsersRequest;
import sme.hub.business.dto.UpdateUserPassword;
import sme.hub.business.repo.UsersRepo;
import sme.hub.business.services.UsersService;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.management.InvalidAttributeValueException;
import java.util.Collections;
import java.util.List;

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
    @Override
    public String getUserIdByUsername(String username) {
        return keycloak.realm(keycloakConfig.getRealm()).users().search(username).stream()
                .filter(user -> username.equalsIgnoreCase(user.getUsername()))
                .findFirst()
                .map(UserRepresentation::getId)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }

    @Override
    public ResponseEntity<String> reset(UpdateUserPassword user) {
        try {
            if(!user.getPassword().equals(user.getConfirmPassword())){
                throw new InvalidAttributeValueException("Invalid password");
            }
            CredentialRepresentation newCredential = new CredentialRepresentation();
            newCredential.setType(CredentialRepresentation.PASSWORD);
            newCredential.setValue(user.getPassword());
            newCredential.setTemporary(false);

            UserResource userResource = keycloak.realm(keycloakConfig.getRealm()).users().get(user.getUserId());
            userResource.resetPassword(newCredential);
            return ResponseEntity.ok("Password changed");
        } catch (InvalidAttributeValueException e){
            return ResponseEntity.badRequest().body("Invalid error: "+ e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: "+ e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> forgot(String username) {
        try{
            String userId = getUserIdByUsername(username);
            UserResource userResource = keycloak.realm(keycloakConfig.getRealm()).users().get(userId);
            List<String> actions = List.of("UPDATE_PASSWORD");
            int lifespanInSeconds = 900;

            userResource.executeActionsEmail(actions, lifespanInSeconds);

            return ResponseEntity.ok("Reset password link sent to user's email.");
        } catch (NotFoundException e){
            return ResponseEntity.internalServerError().body("Notfound Error: "+ e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: "+ e.getMessage());
        }
    }
}
