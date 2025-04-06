package sme.hub.business.services;

import sme.hub.business.dto.CreateUsersRequest;

public interface UsersService {
    void register(CreateUsersRequest request);
    void logout(String refreshToken);
}
