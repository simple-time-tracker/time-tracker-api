package com.dovydasvenckus.timetracker.entry;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
class CreateTimeEntryRequest {

    @NotBlank
    private String taskDescription;
}
