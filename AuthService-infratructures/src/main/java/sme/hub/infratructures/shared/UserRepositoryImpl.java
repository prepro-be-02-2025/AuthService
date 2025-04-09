package sme.hub.infratructures.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sme.hub.business.dto.CreateUsersDto;
import sme.hub.business.repo.UsersRepo;
import sme.hub.infratructures.mapper.UsersMapper;
import sme.hub.infratructures.repo.UsersRepository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UsersRepo {

    private final UsersMapper usersMapper;
    private final UsersRepository usersRepository;

    @Override
    public void create(CreateUsersDto user) {
        usersRepository.save(usersMapper.toUser(user));
    }
}
