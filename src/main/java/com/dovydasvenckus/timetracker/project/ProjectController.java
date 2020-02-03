package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.entry.TimeEntryDTO;
import com.dovydasvenckus.timetracker.entry.TimeEntryService;
import com.dovydasvenckus.timetracker.helper.rest.RestUrlGenerator;
import com.dovydasvenckus.timetracker.helper.security.ClientDetails;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.*;

@Component
@Path("/projects")
public class ProjectController {
    private final RestUrlGenerator restUrlGenerator;

    private final ProjectService projectService;

    private final TimeEntryService timeEntryService;

    public ProjectController(RestUrlGenerator restUrlGenerator,
                             ProjectService projectService,
                             TimeEntryService timeEntryService) {
        this.restUrlGenerator = restUrlGenerator;
        this.projectService = projectService;
        this.timeEntryService = timeEntryService;
    }

    @GET
    @Produces("application/json")
    public List<ProjectReadDTO> getProjects(@Context ClientDetails clientDetails) {
        return projectService.findAllProjects(clientDetails);
    }

    @GET
    @Path("/summaries")
    @Produces("application/json")
    public Page<ProjectReadDTO> getProjectSummaries(@QueryParam("page") Integer page,
                                                    @QueryParam("pageSize") Integer pageSize,
                                                    @Context ClientDetails clientDetails) {
        return projectService.findAllProjectsWithSummaries(page, pageSize, clientDetails);
    }

    @GET
    @Path("{id}/entries")
    @Produces("application/json")
    public Page<TimeEntryDTO> getProjectTimeEntries(@PathParam("id") long id,
                                                    @QueryParam("page") Integer page,
                                                    @QueryParam("pageSize") Integer pageSize,
                                                    @Context ClientDetails clientDetails) {
        return timeEntryService.findAllByProject(id, page, pageSize, clientDetails);
    }

    @GET
    @Path("/active")
    @Produces("application/json")
    public List<ProjectReadDTO> getAllActiveProjects(@Context ClientDetails clientDetails) {
        return projectService.findAllActiveProjects(clientDetails);
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getProject(@PathParam("id") Long id, @Context ClientDetails clientDetails) {
        Optional<ProjectReadDTO> project = projectService.getProjectWithTimeSummary(id, clientDetails);

        return project
                .map(p -> Response.status(OK).entity(p).build())
                .orElse(Response.status(NOT_FOUND).build());
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createProject(@Valid ProjectWriteDTO projectWriteDTO,
                                  @Context UriInfo uriInfo,
                                  @Context ClientDetails clientDetails) {
        Optional<Project> createdProject = projectService.create(projectWriteDTO, clientDetails);

        return createdProject
                .map(ProjectReadDTO::new)
                .map(project ->
                Response.status(CREATED)
                        .entity(project)
                        .header("Location",
                                restUrlGenerator.generateUrlToNewResource(uriInfo, project.getId()))
                        .build())
                .orElse(Response.status(CONFLICT).build());
    }

    @POST
    @Path("{id}/archive")
    public Response archiveProject(@PathParam("id") Long id, @Context ClientDetails clientDetails) {
        boolean wasSuccessfullyArchived = projectService.archiveProject(id, clientDetails);

        return wasSuccessfullyArchived ? Response.ok().build() : Response.status(BAD_REQUEST).build();
    }
}
