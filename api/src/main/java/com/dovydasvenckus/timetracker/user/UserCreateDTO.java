package com.dovydasvenckus.timetracker.user;

import com.dovydasvenckus.timetracker.security.Role;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class UserCreateDTO {
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    @NotEmpty
    private String passwordRepeated;

    @NotNull
    private Role role = Role.USER;
}
