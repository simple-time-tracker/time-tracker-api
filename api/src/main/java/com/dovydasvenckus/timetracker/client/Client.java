package com.dovydasvenckus.timetracker.client;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

@Entity
@Table(name = "clients")
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank
    private String username;

    @Column(name = "password", nullable = false)
    @Length(min = 8)
    private String password;


}
