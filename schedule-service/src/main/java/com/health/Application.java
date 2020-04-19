package com.health;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class Application {

	@RequestMapping("/")
	public String home() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", "Hello World 3");
		return jsonObject.toString();
	}


	@Configuration
	static class OktaOAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
					.authorizeRequests().anyRequest().authenticated()
					.and()
					.oauth2ResourceServer().jwt();
			// @formatter:on
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
