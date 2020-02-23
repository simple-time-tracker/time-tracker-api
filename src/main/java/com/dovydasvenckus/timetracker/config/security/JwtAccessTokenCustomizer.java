package com.dovydasvenckus.timetracker.config.security;

import com.dovydasvenckus.timetracker.core.security.ClientDetails;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.*;

public class JwtAccessTokenCustomizer extends DefaultAccessTokenConverter implements JwtAccessTokenConverterConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(JwtAccessTokenCustomizer.class);

    private static final String CLIENT_NAME_ELEMENT_IN_JWT = "resource_access";

    private static final String ROLE_ELEMENT_IN_JWT = "roles";

    private static final String USER_NAME_ELEMENT_IN_JWT = "user_name";

    private static final String USER_ID_ELEMENT_IN_JWT = "sub";


    private ObjectMapper mapper;

    public JwtAccessTokenCustomizer(ObjectMapper mapper) {
        this.mapper = mapper;
        LOG.info("Initialized {}", JwtAccessTokenCustomizer.class.getSimpleName());
    }

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
        LOG.info("Configured {}", JwtAccessTokenConverter.class.getSimpleName());
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> tokenMap) {
        JsonNode token = mapper.convertValue(tokenMap, JsonNode.class);
        Set<String> audienceList = extractClients(token);
        List<GrantedAuthority> authorities = extractRoles(token);

        OAuth2Authentication authentication = super.extractAuthentication(tokenMap);
        OAuth2Request authRequest = authentication.getOAuth2Request();

        OAuth2Request request =
                new OAuth2Request(authRequest.getRequestParameters(),
                        authRequest.getClientId(),
                        authorities, true,
                        authRequest.getScope(),
                        audienceList, null, null, null);

        ClientDetails clientDetails = extractClientDetails(token);

        Authentication usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(
                clientDetails,
                "N/A",
                authorities
        );

        return new OAuth2Authentication(request, usernamePasswordAuthentication);
    }

    private ClientDetails extractClientDetails(JsonNode jwt) {
        String id = jwt.path(USER_ID_ELEMENT_IN_JWT).textValue();
        String username = jwt.path(USER_NAME_ELEMENT_IN_JWT).textValue();

        if (id != null && username != null) {
            return new ClientDetails(UUID.fromString(id), username);
        }

        throw new IllegalArgumentException("Can't extract username and id from JWT token");
    }

    private List<GrantedAuthority> extractRoles(JsonNode jwt) {
        Set<String> rolesWithPrefix = new HashSet<>();

        jwt.path(CLIENT_NAME_ELEMENT_IN_JWT)
                .elements()
                .forEachRemaining(e -> e.path(ROLE_ELEMENT_IN_JWT)
                        .elements()
                        .forEachRemaining(r -> rolesWithPrefix.add("ROLE_" + r.asText())));

        return AuthorityUtils.createAuthorityList(
                rolesWithPrefix.toArray(new String[0])
        );
    }

    private Set<String> extractClients(JsonNode jwt) {
        if (jwt.has(CLIENT_NAME_ELEMENT_IN_JWT)) {
            JsonNode resourceAccessJsonNode = jwt.path(CLIENT_NAME_ELEMENT_IN_JWT);
            final Set<String> clientNames = new HashSet<>();
            resourceAccessJsonNode.fieldNames()
                    .forEachRemaining(clientNames::add);

            return clientNames;

        } else {
            throw new IllegalArgumentException(
                    "Expected element " + CLIENT_NAME_ELEMENT_IN_JWT + " not found in token"
            );
        }
    }
}
