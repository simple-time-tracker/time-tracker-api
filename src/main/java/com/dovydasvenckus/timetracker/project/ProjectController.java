package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.core.rest.RestUrlGenerator;
import com.dovydasvenckus.timetracker.core.security.ClientDetails;
import com.dovydasvenckus.timetracker.entry.TimeEntryDTO;
import com.dovydasvenckus.timetracker.entry.TimeEntryService;
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
        return projectService.findAllActiveProjects(clientDetails);
    }

    @GET
    @Path("/summaries")
    @Produces("application/json")
    public Page<ProjectReadDTO> getProjectSummaries(@QueryParam("page") int page,
                                                    @QueryParam("pageSize") int pageSize,
                                                    @QueryParam("isArchived") boolean isArchived,
                                                    @Context ClientDetails clientDetails) {
        return projectService.findAllProjectsWithSummaries(page, pageSize, isArchived, clientDetails);
    }

    @GET
    @Path("{id}/entries")
    @Produces("application/json")
    public Page<TimeEntryDTO> getProjectTimeEntries(@PathParam("id") long id,
                                                    @QueryParam("page") int page,
                                                    @QueryParam("pageSize") int pageSize,
                                                    @Context ClientDetails clientDetails) {
        return timeEntryService.findAllByProject(id, page, pageSize, clientDetails);
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
        Optional<ProjectReadDTO> createdProject = projectService.create(projectWriteDTO, clientDetails);

        return createdProject
                .map(project ->
                        Response.status(CREATED)
                                .entity(project)
                                .location(restUrlGenerator.generateUrlToNewResource(uriInfo, project.getId()))
                                .build())
                .orElse(Response.status(CONFLICT).build());
    }


    @PUT
    @Path("{id}")
    public Response updateProject(@PathParam("id") long id,
                                  @Valid ProjectWriteDTO createRequest,
                                  @Context ClientDetails clientDetails,
                                  @Context UriInfo uriInfo) {
        Optional<ProjectReadDTO> updatedProject = projectService.updateProject(id, createRequest, clientDetails);
        if (updatedProject.isPresent()) {
            return Response.noContent().build();
        }

        return projectService.create(createRequest, clientDetails)
                .map(newProject -> Response
                        .created(restUrlGenerator.generateUrlToNewResource(uriInfo, newProject.getId()))
                        .build())
                .orElse(Response.serverError().build());
    }

    @POST
    @Path("{id}/archive")
    public Response archiveProject(@PathParam("id") Long id, @Context ClientDetails clientDetails) {
        boolean wasSuccessfullyArchived = projectService.archiveProject(id, clientDetails);

        return wasSuccessfullyArchived ? Response.ok().build() : Response.status(BAD_REQUEST).build();
    }

    @POST
    @Path("{id}/restore")
    public Response restoreProject(@PathParam("id") Long id, @Context ClientDetails clientDetails) {
        boolean wasUnarchived = projectService.restoreProject(id, clientDetails);

        return wasUnarchived ? Response.ok().build() : Response.status(BAD_REQUEST).build();
    }
}
