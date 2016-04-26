package com.dovydasvenckus.timetracker.entry;

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
@Path("/timeEntries")
public class TimeEntryController {

    @Context
    UriInfo uriInfo;

    @Autowired
    RestUrlGenerator restUrlGenerator;

    @Autowired
    TimeEntryRepository timeEntryRepository;

    @GET
    @Produces("application/json")
    public List<TimeEntry> getAll() {
        return timeEntryRepository.findAll();
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
