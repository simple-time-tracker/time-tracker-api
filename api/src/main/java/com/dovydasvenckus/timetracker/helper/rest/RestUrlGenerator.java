package com.dovydasvenckus.timetracker.helper.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Component
public class RestUrlGenerator {

    private final Integer serverPort;

    @Autowired
    public RestUrlGenerator(@Value("${server.port}") Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String generateUrlToNewResource(UriInfo uriInfo, Object identifier) {
        UriBuilder urlBuilder = uriInfo.getRequestUriBuilder();

        return urlBuilder
                .path(identifier.toString())
                .port(serverPort)
                .build().toString();
    }

}
