package com.lukdut.monitoring.backend.repository;

import com.lukdut.monitoring.backend.model.User;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

    Long countByRole(String role);
}
