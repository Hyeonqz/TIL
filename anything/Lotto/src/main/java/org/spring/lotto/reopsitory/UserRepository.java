package org.spring.lotto.reopsitory;

import org.spring.lotto.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
