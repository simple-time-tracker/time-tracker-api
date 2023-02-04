package com.dovydasvenckus.timetracker.project;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ProjectWriteDTO {

    @NotBlank
    private String name;

}
