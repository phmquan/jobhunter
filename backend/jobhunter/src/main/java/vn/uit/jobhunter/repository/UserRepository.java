package vn.uit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.uit.jobhunter.domain.Role;
import vn.uit.jobhunter.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByEmail(String email);

    User findByRefreshTokenAndEmail(String refreshToken, String email);

    public boolean existsByEmail(String email);

    Long countByRoleNot(Role roleName);

}
