package com.health;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import feign.RequestInterceptor;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrix
@EnableHystrixDashboard
@EnableZuulProxy
public class Application {


    @Bean
    public Prefilter preFilter() {
        return new Prefilter();
    }

    @Configuration
	@EnableWebSecurity
    static class OktaOAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http.
                    csrf().disable()
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .oauth2Login();

            // @formatter:on
        }
    }


//	@Configuration
//	static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//		@Override
//		public void configure(WebSecurity web) throws Exception {
//			// @formatter:off
//			web.ignoring().antMatchers("/**");
//			// @formatter:on
//		}
//	}

    @Bean
    public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
		config.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public RequestInterceptor getUserFeignClientInterceptor(OAuth2AuthorizedClientService clientService) {
        return new UserFeignClientInterceptor(clientService);
    }

    @Bean
    public AuthorizationHeaderFilter authHeaderFilter(OAuth2AuthorizedClientService clientService) {
        return new AuthorizationHeaderFilter(clientService);
    }

    @Autowired
    private MyFeignClient myFeignClient;

    @RequestMapping("/")
    @HystrixCommand(fallbackMethod = "homeFallback")
    public String home() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Hello World");
        jsonObject.put("message-2", new JSONObject(myFeignClient.client2Response()));
        return jsonObject.toString();
    }

    public String homeFallback() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Hello World");
        jsonObject.put("message-2", "Hello World 8002 is down");
        return jsonObject.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
