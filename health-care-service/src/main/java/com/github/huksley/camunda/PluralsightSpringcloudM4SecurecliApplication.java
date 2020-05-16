package com.github.huksley.camunda;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


public class PluralsightSpringcloudM4SecurecliApplication {

    public static void main(String[] args) {

        System.out.println("cron job started");

        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
//        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.header);
        resourceDetails.setAccessTokenUri("https://dev-922609.okta.com/oauth2/default/v1/token");

        //must be a valid scope or get an error; if empty, get all scopes (default); better to ask for one
        resourceDetails.setScope(Arrays.asList("custom_mod"));
        resourceDetails.setGrantType("client_credentials");

        //must be valid client id or get an error
        resourceDetails.setClientId("0oaced70aOl8GOCz24x6");
        resourceDetails.setClientSecret("NjDhS4lkvTP0z6bVTrdOlFyzj9QetzPN6QTXnqCC");

        //diff user results in diff authorities/roles coming out; preauth on roles fails for adam, works for barry
//        resourceDetails.setUsername("socrates.hlapolosa@gmail.com");
//        resourceDetails.setPassword("Qwaszx!2345");

        OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails);
        //could also get scopes: template.getAccessToken().getScope()
        String token =  template.getAccessToken().toString();//.getValue();

        System.out.println("Token: " + token);

        String s = template.getForObject("http://localhost:8050/home", String.class);

        System.out.println("Result: " + s);
    }
}
