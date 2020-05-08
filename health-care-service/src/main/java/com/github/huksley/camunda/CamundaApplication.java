package com.github.huksley.camunda;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableProcessApplication
@EnableScheduling
@EnableEurekaClient
@RestController
public class CamundaApplication {


    @RequestMapping("/")
    public String home() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Hello World 3");
        return jsonObject.toString();
    }

//    @Configuration
//    static class OktaOAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            // @formatter:off
//            http
//                    .authorizeRequests().anyRequest().authenticated()
//                    .and()
//                    .oauth2ResourceServer().jwt();
//            // @formatter:on
//        }
//    }


    public static void main(String... args) {
        SpringApplication.run(CamundaApplication.class, args);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
