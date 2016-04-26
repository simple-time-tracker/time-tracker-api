package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.helper.rest.RestUrlGenerator;
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

    @Autowired
    RestUrlGenerator restUrlGenerator;

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

        return Response.status(Response.Status.CREATED)
                .entity("New project has been created")
                .header("Location",
                        restUrlGenerator.generateUrlToNewResource(uriInfo, project.getId())
                        ).build();
    }
}
