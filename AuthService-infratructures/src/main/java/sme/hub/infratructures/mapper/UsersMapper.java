package sme.hub.infratructures.mapper;

import org.mapstruct.Mapper;
import sme.hub.business.dto.CreateUsersDto;
import sme.hub.infratructures.model.Users;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    Users toUser(CreateUsersDto user);
}
