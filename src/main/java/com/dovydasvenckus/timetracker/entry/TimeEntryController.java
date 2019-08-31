package com.dovydasvenckus.timetracker.entry;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

import com.dovydasvenckus.timetracker.helper.rest.RestUrlGenerator;
import java.util.Optional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@Path("/entries")
public class TimeEntryController {

    @Context
    private UriInfo uriInfo;

    private final RestUrlGenerator restUrlGenerator;

    private final TimeEntryService timeEntryService;

    @Autowired
    public TimeEntryController(RestUrlGenerator restUrlGenerator,
                               TimeEntryService timeEntryService) {
        this.restUrlGenerator = restUrlGenerator;
        this.timeEntryService = timeEntryService;
    }

    @GET
    @Produces("application/json")
    public Page<TimeEntryDTO> getAll(@QueryParam("page") int page) {
        return timeEntryService.findAll(page);
    }

    @GET
    @Produces("application/json")
    @Path("/current")
    public TimeEntryDTO getCurrent() {
        Optional<TimeEntryDTO> current = timeEntryService.findCurrentlyActive();

        return current.orElse(null);
    }

    @POST
    @Path("/start/{project}")
    @Produces("text/plain")
    public Response startTracking(@PathParam("project") long projectId,
                                  @Valid @RequestBody CreateTimeEntryRequest request
    ) {
        Optional<TimeEntryDTO> current = timeEntryService.findCurrentlyActive();

        if (current.isEmpty()) {
            TimeEntry timeEntry = timeEntryService.createTimeEntry(projectId, request.getTaskDescription());

            return Response.status(CREATED)
                       .entity("New time entry has been created")
                       .header("Location", restUrlGenerator.generateUrlToNewResource(uriInfo, timeEntry.getId()))
                       .build();
        }

        return Response.status(INTERNAL_SERVER_ERROR).entity("You are already tracking time on project").build();
    }

    @POST
    @Path("/stop")
    @Produces("text/plain")
    public Response stopCurrent() {
        Optional<TimeEntryDTO> current = timeEntryService.findCurrentlyActive();

        if (current.isPresent()) {
            timeEntryService.stop(current.get());
            return Response.status(OK).build();
        }

        return Response.status(INTERNAL_SERVER_ERROR).entity("No active task").build();
    }

    @POST
    @Consumes("application/json")
    @Produces("text/html")
    public Response createTimeEntry(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = timeEntryService.create(timeEntryDTO);

        return Response.status(CREATED)
                   .entity("New time entry has been created")
                   .header("Location",
                           restUrlGenerator.generateUrlToNewResource(uriInfo, timeEntry.getId())
                   ).build();
    }

    @DELETE
    @Path("/{project}")
    public Response deleteProject(@PathParam("project") long projectId) {
        timeEntryService.delete(projectId);

        return Response.status(NO_CONTENT).build();
    }
}
