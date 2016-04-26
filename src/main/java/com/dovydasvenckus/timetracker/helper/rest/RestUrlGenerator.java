package com.dovydasvenckus.timetracker.helper.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Component
public class RestUrlGenerator {

    @Value("${server.port}")
    private Integer serverPort;

    public String generateUrlToNewResource(UriInfo uriInfo, Object identifier){
        UriBuilder urlBuilder = uriInfo.getRequestUriBuilder();

        return urlBuilder
                .path(identifier.toString())
                .port(serverPort)
                .build().toString();
    }

}
