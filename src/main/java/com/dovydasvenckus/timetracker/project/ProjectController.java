package com.dovydasvenckus.timetracker.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Component
@Path("/projects")
public class ProjectController {
    @Context
    UriInfo uriInfo;

    @Value("${server.port}")
    private Integer serverPort;

    @Autowired
    ProjectRepository projectRepository;

    @GET
    @Produces("application/json")
    public List<Project> getProject(){
        return projectRepository.findAll();
    }

    @POST
    @Consumes("application/json")
    @Produces("text/html")
    public Response createProject(Project project){
        projectRepository.save(project);
        UriBuilder resourceUrlBuilder = uriInfo.getRequestUriBuilder();

        return Response.status(Response.Status.CREATED)
                .entity("New project has been created")
                .header("Location",
                        resourceUrlBuilder
                                .path(project.getId().toString())
                                .port(serverPort)
                                .build()
                        ).build();
    }
}
