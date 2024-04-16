package maple.cube.random.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import maple.cube.random.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
