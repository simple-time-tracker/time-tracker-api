package com.dovydasvenckus.timetracker.client;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
class ClientRegisterDTO {

    @NotNull
    @Length(min = 4, max = 128)
    private String username;

    @NotNull
    @Length(min = 8, max = 128)
    private String password;
}
