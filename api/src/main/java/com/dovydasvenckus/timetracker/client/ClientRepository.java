package com.dovydasvenckus.timetracker.client;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByUsername(String username);
}
