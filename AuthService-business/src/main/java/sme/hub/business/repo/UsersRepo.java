package sme.hub.business.repo;

import sme.hub.business.dto.CreateUsersDto;

public interface UsersRepo {

    void create(CreateUsersDto user);
}
