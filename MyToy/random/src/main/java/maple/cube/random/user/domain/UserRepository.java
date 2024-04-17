package maple.cube.random.user.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import maple.cube.random.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
