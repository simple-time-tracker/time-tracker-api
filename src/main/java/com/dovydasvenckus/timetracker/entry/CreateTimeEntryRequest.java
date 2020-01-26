package com.dovydasvenckus.timetracker.entry;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
class CreateTimeEntryRequest {

    @NotNull
    @NotBlank
    private String taskDescription;
}
