package com.photo.app.api.users.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.photo.app.api.users.service.UsersService;
import com.photo.app.api.users.shared.UserDto;
import com.photo.app.api.users.ui.model.LoginRequestModel;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private UsersService usersService;
	private Environment environment;

	public AuthenticationFilter(AuthenticationManager authenticationManager, UsersService usersService,
			Environment environment) {
		super(authenticationManager);
		this.usersService = usersService;
		this.environment = environment;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {

			LoginRequestModel loginRequestModel = new ObjectMapper().readValue(req.getInputStream(),
					LoginRequestModel.class);

			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
					loginRequestModel.getEmail(), loginRequestModel.getPassword(), new ArrayList<>()));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		String username = ((User) auth.getPrincipal()).getUsername();
		UserDto userDto = usersService.getUserDetailsByEmail(username);
		String userId = userDto.getUserId();

		Instant now = Instant.now();
		String tokenSecret = environment.getProperty("token.secret");
		byte[] secretKeyByts = Base64.encode(tokenSecret.getBytes());
		SecretKey secretKey = new SecretKeySpec(secretKeyByts, SignatureAlgorithm.HS512.getJcaName());

		String token = Jwts.builder().setSubject(userId)
				.setExpiration(
						Date.from(now.plusMillis(Long.parseLong(environment.getProperty("token.expiration_time")))))
				.setIssuedAt(Date.from(now)).signWith(secretKey, SignatureAlgorithm.HS512).compact();
		
		res.addHeader("token", token);
		res.addHeader("userId", userId);
	}
}
