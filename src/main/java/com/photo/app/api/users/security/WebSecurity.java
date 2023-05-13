package com.photo.app.api.users.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.photo.app.api.users.service.UsersService;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	private UsersService usersService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private Environment environment;

	public WebSecurity(UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder,
			Environment environment) {
		this.usersService = usersService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.environment = environment;
	}

	@Bean
	protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

		// Configure AuthenticationManagerBuilder
		AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity
				.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

		// Create AuthenticationFilter
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, usersService,
				environment);
		authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));

		httpSecurity.csrf().disable();

		httpSecurity.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/users/**")).permitAll()
//				.requestMatchers(HttpMethod.POST, "/users")
//				.access(new WebExpressionAuthorizationManager("hasIpAddress('" + environment.getProperty("gateway.ip") + "')"))
				.requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll().and()
				.addFilter(authenticationFilter).authenticationManager(authenticationManager).sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		httpSecurity.headers().frameOptions().disable();

		return httpSecurity.build();
	}
}
