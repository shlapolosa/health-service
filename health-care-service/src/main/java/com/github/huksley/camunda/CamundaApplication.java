package com.github.huksley.camunda;

import com.okta.spring.boot.oauth.Okta;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

@SpringBootApplication
@EnableProcessApplication
@EnableScheduling
@EnableEurekaClient
@RestController
public class CamundaApplication {

    private final static Logger log = LoggerFactory.getLogger(CamundaApplication.class);

//    @Autowired
//    ProspectRepository prospectRepository;

    @Autowired
    ProspectService prospectService;

    @GetMapping("/home")
    public String howdy(Principal principal) {
        String username = principal.getName();
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        log.info("claims: " + token.getTokenAttributes());
        return "Hello, "+ username ;
    }

    @GetMapping(value = "/prospect/process/{processID}")
    public Prospect getProspectByProcess(@PathVariable String processID) {

        Optional<Prospect> prospect = prospectService.getProspectByProcess(processID);
        return prospect.orElse(new Prospect());
    }

    @GetMapping(value = "/prospect/{id}")
    public Prospect getProspectByID(@PathVariable Long id) {
        Optional<Prospect> prospect = prospectService.getProspectByID(id);
        return prospect.orElse(new Prospect());
    }

    @GetMapping(value = "/prospect/businesskey/{idnumber}")
    public Prospect getProspectByIDNumber(@PathVariable String idnumber) {
        Optional<Prospect> prospect = prospectService.getProspectByIDNumber(idnumber);
        return prospect.orElse(new Prospect());
    }

    @Configuration
    static class OktaOAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/home","/engine-rest/**").authenticated()
                    .anyRequest()
                    .permitAll()
                    .and()
                    .oauth2ResourceServer().jwt();

            Okta.configureResourceServer401ResponseBody(http);
            // @formatter:on
        }
    }


    public static void main(String... args) {
        SpringApplication.run(CamundaApplication.class, args);
    }

    @Bean
    @ConfigurationProperties(prefix = "example.oauth2.client")
    protected ClientCredentialsResourceDetails oAuthDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    @ConditionalOnMissingBean
    protected RestTemplate restTemplate() {
        return new OAuth2RestTemplate(oAuthDetails());
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
