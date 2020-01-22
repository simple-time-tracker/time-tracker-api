package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.helper.rest.RestUrlGenerator;
import com.dovydasvenckus.timetracker.helper.security.ClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.*;

@Component
@Path("/entries")
public class TimeEntryController {

    @Context
    private UriInfo uriInfo;

    @Context
    private ClientDetails clientDetails;

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
    public Page<TimeEntryDTO> getAll(@QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
        return timeEntryService.findAll(page, pageSize, clientDetails);
    }

    @GET
    @Produces("application/json")
    @Path("/current")
    public TimeEntryDTO getCurrent() {
        Optional<TimeEntryDTO> current = timeEntryService.findCurrentlyActive(clientDetails);

        return current.orElse(null);
    }

    @POST
    @Path("/start/{project}")
    @Produces("text/plain")
    public Response startTracking(@PathParam("project") long projectId,
                                  @Valid @RequestBody CreateTimeEntryRequest request
    ) {
        Optional<TimeEntryDTO> current = timeEntryService.findCurrentlyActive(clientDetails);

        if (current.isEmpty()) {
            TimeEntry timeEntry = timeEntryService.startTracking(
                    projectId,
                    request.getTaskDescription(),
                    clientDetails
            );

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
        Optional<TimeEntryDTO> current = timeEntryService.findCurrentlyActive(clientDetails);

        if (current.isPresent()) {
            timeEntryService.stop(current.get(), clientDetails);
            return Response.status(OK).build();
        }

        return Response.status(INTERNAL_SERVER_ERROR).entity("No active task").build();
    }

    @POST
    @Consumes("application/json")
    @Produces("text/html")
    public Response createTimeEntry(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry = timeEntryService.create(timeEntryDTO, clientDetails);

        return Response.status(CREATED)
                .entity("New time entry has been created")
                .header("Location",
                        restUrlGenerator.generateUrlToNewResource(uriInfo, timeEntry.getId())
                ).build();
    }

    @DELETE
    @Path("/{project}")
    public Response deleteProject(@PathParam("project") long projectId) {
        timeEntryService.delete(projectId, clientDetails);

        return Response.status(NO_CONTENT).build();
    }
}
