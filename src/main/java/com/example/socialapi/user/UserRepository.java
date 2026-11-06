package com.example.socialapi.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Spring Data generates the implementation at startup from the method names. */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
