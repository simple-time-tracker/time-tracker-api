package com.dovydasvenckus.timetracker.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

@Configuration
@Conditional(value = {Oauth2RestTemplateConfig.ServiceAccountEnabled.class})
public class Oauth2RestTemplateConfig {
    private static final Logger LOG = LoggerFactory.getLogger(Oauth2RestTemplateConfig.class);

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails details) {
        OAuth2RestTemplate oauth2RestTemplate = new OAuth2RestTemplate(details);

        LOG.debug("Begin OAuth2RestTemplate: getAccessToken");
        oauth2RestTemplate.getAccessToken();
        LOG.debug("End OAuth2RestTemplate: getAccessToken");
        return oauth2RestTemplate;
    }

    static class ServiceAccountEnabled extends AllNestedConditions {

        ServiceAccountEnabled() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "rest.security", value = "enabled", havingValue = "true")
        static class SecurityEnabled {
        }

        @ConditionalOnProperty(
                prefix = "security.oauth2.client",
                value = "grant-type",
                havingValue = "client_credentials"
        )
        static class ClientCredentialConfigurationExists {
        }

    }

}