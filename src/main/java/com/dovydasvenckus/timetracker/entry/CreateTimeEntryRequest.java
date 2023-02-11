package com.dovydasvenckus.timetracker.entry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
class CreateTimeEntryRequest {

    @NotNull
    @NotBlank
    private String taskDescription;
}
