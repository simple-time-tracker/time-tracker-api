package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.core.rest.RestUrlGenerator;
import com.dovydasvenckus.timetracker.core.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Component
@RestController
@AllArgsConstructor
@RequestMapping("/entries")
public class TimeEntryController {

    private final RestUrlGenerator restUrlGenerator;
    private final JwtService jwtService;
    private final TimeEntryService timeEntryService;

    @GetMapping
    public Page<TimeEntryDTO> getAll(@RequestParam("page") int page,
                                     @RequestParam("pageSize") int pageSize,
                                     @AuthenticationPrincipal Jwt jwt) {
        return timeEntryService.findAll(page, pageSize, jwtService.getUserId(jwt));
    }

    @GetMapping(value = "/current")
    public TimeEntryDTO getCurrent(@AuthenticationPrincipal Jwt jwt) {
        Optional<TimeEntryDTO> current = timeEntryService.findCurrentlyActive(jwtService.getUserId(jwt));

        return current.orElse(null);
    }

    @PostMapping("/start/{project}")
    public ResponseEntity startTracking(@PathVariable("project") long projectId,
                                        @Valid @RequestBody CreateTimeEntryRequest request,
                                        @AuthenticationPrincipal Jwt jwt
    ) {
        Optional<TimeEntryDTO> current = timeEntryService.findCurrentlyActive(jwtService.getUserId(jwt));

        if (current.isEmpty()) {
            TimeEntry timeEntry = timeEntryService.startTracking(
                    projectId,
                    request.getTaskDescription(),
                    jwtService.getUserId(jwt)
            );

            return ResponseEntity.created(restUrlGenerator.generateUrlToNewResource(timeEntry.getId().toString()))
                    .build();
        }

        return ResponseEntity.internalServerError().body("You are already tracking time on project");
    }

    @PostMapping("/stop")
    public ResponseEntity stopCurrent(@AuthenticationPrincipal Jwt jwt) {
        Optional<TimeEntryDTO> current = timeEntryService.findCurrentlyActive(jwtService.getUserId(jwt));

        if (current.isPresent()) {
            timeEntryService.stop(current.get(), jwtService.getUserId(jwt));
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.internalServerError().body("No active task");
    }

    @PostMapping
    public ResponseEntity createTimeEntry(@RequestBody TimeEntryDTO timeEntryDTO, @AuthenticationPrincipal Jwt jwt) {
        TimeEntry timeEntry = timeEntryService.create(timeEntryDTO, jwtService.getUserId(jwt));

        return ResponseEntity.created(restUrlGenerator.generateUrlToNewResource(timeEntry.getId().toString())).build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable("id") long id, @AuthenticationPrincipal Jwt jwt) {
        timeEntryService.delete(id, jwtService.getUserId(jwt));
    }
}
