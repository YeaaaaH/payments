package payments.duo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payments.duo.model.auth.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}