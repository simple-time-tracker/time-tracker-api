package com.dovydasvenckus.timetracker.user;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {
    Optional<User> findOne(Long id);

    Optional<User> findOneByEmail(String email);

    List<User> findAll();

    User save(User user);
}
