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
    UriInfo uriInfo;

    @Autowired
    RestUrlGenerator restUrlGenerator;

    @Autowired
    ProjectService projectService;

    @GET
    @Produces("application/json")
    public List<ProjectDTO> getProjects() {
        return projectService.findAllProjects();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getProject(@PathParam("id") Long id) {
        Optional<ProjectDTO> project = projectService.findProject(id);

        return project
                .map(p -> Response.status(OK).entity(p).build())
                .orElse(Response.status(NOT_FOUND).build());
    }

    @POST
    @Consumes("application/json")
    @Produces("text/html")
    public Response createProject(ProjectDTO projectDTO) {
        Optional<Project> project = projectService.create(projectDTO);

        return project.map(p ->
                Response.status(CREATED)
                .entity("New projectDTO has been created")
                .header("Location",
                        restUrlGenerator.generateUrlToNewResource(uriInfo, p.getId())).build())
                .orElse(Response.status(CONFLICT).build());
    }
}
