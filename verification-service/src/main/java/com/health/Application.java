package com.health;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrixDashboard
public class Application {
	
	@Autowired
	private MyFeignClient myFeignClient;

	@RequestMapping("/")
	public String home() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", "Hello World 2");
		jsonObject.put("message-3", new JSONObject(myFeignClient.client3Response()));
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
