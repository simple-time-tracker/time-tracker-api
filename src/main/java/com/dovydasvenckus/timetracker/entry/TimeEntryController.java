package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.helper.rest.RestUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.List;

import static javax.ws.rs.core.Response.Status.*;

@Component
@Path("/entries")
public class TimeEntryController {

    @Context
    UriInfo uriInfo;

    @Autowired
    RestUrlGenerator restUrlGenerator;

    @Autowired
    TimeEntryService timeEntryService;

    @Autowired
    TimeEntryRepository timeEntryRepository;

    @GET
    @Produces("application/json")
    public List<TimeEntry> getAll() {
        return timeEntryRepository.findAll();
    }

    @GET
    @Produces("application/json")
    @Path("/current")
    public TimeEntry getCurrent() {
        return timeEntryRepository.findCurrentlyActive();
    }

    @POST
    @Path("/start/{project}")
    @Produces("text/plain")
    public Response startTracking(@PathParam("project") long projectId, @QueryParam("description") String description) {
        TimeEntry current = timeEntryRepository.findCurrentlyActive();

        if (current == null) {
            TimeEntry timeEntry = timeEntryService.createTimeEntry(projectId, description);

            return Response.status(CREATED)
                    .entity("New time entry has been created")
                    .header("Location",
                            restUrlGenerator.generateUrlToNewResource(uriInfo, timeEntry.getId())
                    ).build();
        }

        return Response.status(INTERNAL_SERVER_ERROR).entity("You are already tracking time on project").build();
    }

    @POST
    @Path("/stop")
    @Produces("text/plain")
    public Response stopCurrent() {
        TimeEntry current = timeEntryRepository.findCurrentlyActive();

        if (current != null) {
            current.setEndDate(LocalDateTime.now());
            timeEntryRepository.save(current);
            return Response.status(OK).build();
        }

        return Response.status(INTERNAL_SERVER_ERROR).entity("No active task").build();
    }

    @POST
    @Consumes("application/json")
    @Produces("text/html")
    public Response createTimeEntry(TimeEntry timeEntry) {
        timeEntryRepository.save(timeEntry);

        return Response.status(CREATED)
                .entity("New time entry has been created")
                .header("Location",
                        restUrlGenerator.generateUrlToNewResource(uriInfo, timeEntry.getId())
                ).build();
    }
}
