package payments.duo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import payments.duo.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
