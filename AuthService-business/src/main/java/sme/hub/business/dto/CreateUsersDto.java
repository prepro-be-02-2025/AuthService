package sme.hub.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUsersDto {
    private String name;
    private String email;
    private String username;
    private String keycloakId;
}
