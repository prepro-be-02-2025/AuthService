package sme.hub.business.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import sme.hub.business.dto.CreateUsersRequest;
import sme.hub.business.dto.UpdateUserPassword;

public interface UsersService {
    void register(CreateUsersRequest request);
    void logout(String refreshToken);
    String getUserIdByUsername(String username);
    String reset(UpdateUserPassword user);
    String forgot(String username);

}
