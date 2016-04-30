package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.helper.rest.RestUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CREATED;

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

    @POST
    @Consumes("application/json")
    @Produces("text/html")
    public Response createProject(ProjectDTO projectDTO) {
        Project project = projectService.create(projectDTO);

        return Response.status(CREATED)
                .entity("New projectDTO has been created")
                .header("Location",
                        restUrlGenerator.generateUrlToNewResource(uriInfo, project.getId())
                ).build();
    }
}
