package uk.ac.ox.ctl.lti13.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.RestOperations;
import uk.ac.ox.ctl.lti13.Lti13Configurer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableWebSecurity
public class Lti13Configuration extends WebSecurityConfigurerAdapter {
    
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();
        Lti13Configurer lti13Configurer = new Lti13Configurer();
        http.apply(lti13Configurer);
    }

    @Bean
    public KeyPair keyPair() throws NoSuchAlgorithmException {
        return KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }

    @Bean
    public RestOperations restOperations() {
        return Mockito.mock(RestOperations.class);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(KeyPair keyPair) {
        String platformUri = "https://platform.test/";

        ClientRegistration client = ClientRegistration.withRegistrationId("test")
                .clientId("test-id")
                .authorizationGrantType(AuthorizationGrantType.IMPLICIT)
                .scope("openid")
                .redirectUriTemplate("{baseUrl}/lti/login")
                .authorizationUri(platformUri+ "/auth/new")
                .tokenUri(platformUri+ "/access_tokens")
                .jwkSetUri(platformUri+ "/keys.json")
                .build();
        ClientRegistrationRepository clientRegistrationRepository = new InMemoryClientRegistrationRepository(client);
        return clientRegistrationRepository;
    }
}


