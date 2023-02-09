package com.dovydasvenckus.timetracker.project;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ProjectWriteDTO {

    @NotBlank
    private String name;

}
