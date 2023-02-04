package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.core.rest.RestUrlGenerator;
import com.dovydasvenckus.timetracker.core.security.JwtService;
import com.dovydasvenckus.timetracker.entry.TimeEntryDTO;
import com.dovydasvenckus.timetracker.entry.TimeEntryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Component
@RestController
@AllArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final RestUrlGenerator restUrlGenerator;
    private final JwtService jwtService;
    private final ProjectService projectService;
    private final TimeEntryService timeEntryService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProjectReadDTO> getProjects(@AuthenticationPrincipal Jwt jwt) {
        return projectService.findAllActiveProjects(jwtService.getUserId(jwt));
    }

    @GetMapping("/summaries")
    public Page<ProjectReadDTO> getProjectSummaries(@RequestParam("page") int page,
                                                    @RequestParam("pageSize") int pageSize,
                                                    @RequestParam("isArchived") boolean isArchived,
                                                    @AuthenticationPrincipal Jwt jwt) {
        return projectService.findAllProjectsWithSummaries(page, pageSize, isArchived, jwtService.getUserId(jwt));
    }

    @GetMapping("/{id}/entries")
    public Page<TimeEntryDTO> getProjectTimeEntries(@PathVariable("id") long id,
                                                    @RequestParam("page") int page,
                                                    @RequestParam("pageSize") int pageSize,
                                                    @AuthenticationPrincipal Jwt jwt) {
        return timeEntryService.findAllByProject(id, page, pageSize, jwtService.getUserId(jwt));
    }

    @GetMapping("/{id}")
    public ResponseEntity getProject(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        Optional<ProjectReadDTO> project = projectService.getProjectWithTimeSummary(id, jwtService.getUserId(jwt));

        return project
                .map(p -> ResponseEntity.ok((p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity createProject(@RequestBody @Valid ProjectWriteDTO projectWriteDTO,
                                        @AuthenticationPrincipal Jwt jwt) {
        Optional<ProjectReadDTO> createdProject = projectService.create(projectWriteDTO, jwtService.getUserId(jwt));

        return createdProject
                .map(project ->
                        ResponseEntity.created(restUrlGenerator.generateUrlToNewResource(project.getId().toString()))
                                .body(project)
                )
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }


    @PutMapping("/{id}")
    public ResponseEntity updateProject(@PathVariable("id") long id,
                                        @RequestBody @Valid ProjectWriteDTO createRequest,
                                        @AuthenticationPrincipal Jwt jwt) {
        Optional<ProjectReadDTO> updatedProject = projectService.updateProject(
                id,
                createRequest,
                jwtService.getUserId(jwt)
        );
        if (updatedProject.isPresent()) {
            return ResponseEntity.noContent().build();
        }

        return projectService.create(createRequest, jwtService.getUserId(jwt))
                .map(newProject -> ResponseEntity
                        .created(restUrlGenerator.generateUrlToNewResource(newProject.getId().toString()))
                        .build())
                .orElse(ResponseEntity.internalServerError().build());
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity archiveProject(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        boolean wasSuccessfullyArchived = projectService.archiveProject(id, jwtService.getUserId(jwt));

        return wasSuccessfullyArchived
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity restoreProject(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        boolean wasUnarchived = projectService.restoreProject(id, jwtService.getUserId(jwt));

        return wasUnarchived ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
