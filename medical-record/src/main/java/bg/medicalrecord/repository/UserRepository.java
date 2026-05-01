package bg.medicalrecord.repository;

import bg.medicalrecord.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Хранилище за потребителски акаунти
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
