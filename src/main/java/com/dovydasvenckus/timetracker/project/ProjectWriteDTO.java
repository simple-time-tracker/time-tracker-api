package com.dovydasvenckus.timetracker.project;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectWriteDTO {

    @NotBlank
    private String name;

}
