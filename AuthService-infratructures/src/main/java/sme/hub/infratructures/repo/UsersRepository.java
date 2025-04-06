package sme.hub.infratructures.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sme.hub.infratructures.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
}
