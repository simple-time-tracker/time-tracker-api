package com.dovydasvenckus.timetracker.core.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class RestUrlGenerator {

    private final String apiUrl;

    @Autowired
    public RestUrlGenerator(@Value("${server.servlet.context-path}") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    //TODO Generate real url
    public URI generateUrlToNewResource(String identifier) {
        return URI.create(identifier);
    }

}
