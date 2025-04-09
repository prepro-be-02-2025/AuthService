package sme.hub.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPassword {
    @NotBlank
    String userId;

    @NotBlank
    @Pattern(regexp = "^(?=.*\\d).{4,8}$", flags = Pattern.Flag.UNICODE_CASE)
    String password;

    @NotBlank
    @Pattern(regexp = "^(?=.*\\d).{4,8}$", flags = Pattern.Flag.UNICODE_CASE)
    String confirmPassword;
}
