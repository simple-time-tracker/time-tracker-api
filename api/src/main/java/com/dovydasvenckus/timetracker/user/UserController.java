package com.dovydasvenckus.timetracker.user;

import com.dovydasvenckus.timetracker.helper.rest.RestUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Component
@Path("/users")
public class UserController {
    @Context
    UriInfo uriInfo;

    RestUrlGenerator restUrlGenerator;


    @Autowired
    public UserController(RestUrlGenerator restUrlGenerator) {
        this.restUrlGenerator = restUrlGenerator;
    }

    @POST
    @Consumes("application/json")
    @Produces("text/html")
    public Response createUser(UserCreateDTO createUserDto) {
        try {
            //userService.create(createUserDto);
        } catch (DataIntegrityViolationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User name is already taken").build();
        }
        return Response.ok().build();
    }
}
