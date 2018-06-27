package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.helper.rest.RestUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Context
    private UriInfo uriInfo;

    private final RestUrlGenerator restUrlGenerator;

    private final ProjectService projectService;

    @Autowired
    public ProjectController(RestUrlGenerator restUrlGenerator, ProjectService projectService) {
        this.restUrlGenerator = restUrlGenerator;
        this.projectService = projectService;
    }

    @GET
    @Produces("application/json")
    public List<ProjectReadDTO> getProjects() {
        return projectService.findAllProjects();
    }

    @GET
    @Path("/active")
    @Produces("application/json")
    public List<ProjectReadDTO> getAllActiveProjects() {
        return projectService.findAllActiveProjects();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getProject(@PathParam("id") Long id) {
        Optional<ProjectReadDTO> project = projectService.findProject(id);

        return project
                .map(p -> Response.status(OK).entity(p).build())
                .orElse(Response.status(NOT_FOUND).build());
    }

    @POST
    @Consumes("application/json")
    @Produces("text/html")
    public Response createProject(ProjectWriteDTO projectWriteDTO) {
        Optional<Project> project = projectService.create(projectWriteDTO);

        return project.map(p ->
                Response.status(CREATED)
                        .entity("New project has been created")
                        .header("Location",
                                restUrlGenerator.generateUrlToNewResource(uriInfo, p.getId())).build())
                .orElse(Response.status(CONFLICT).build());
    }

    @POST
    @Path("{id}/archive")
    public Response archiveProject(@PathParam("id") Long id) {
        boolean wasSuccessfullyArchived = projectService.archiveProject(id);

        return wasSuccessfullyArchived ? Response.ok().build() : Response.status(BAD_REQUEST).build();
    }
}
