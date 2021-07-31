package payments.duo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payments.duo.model.auth.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}