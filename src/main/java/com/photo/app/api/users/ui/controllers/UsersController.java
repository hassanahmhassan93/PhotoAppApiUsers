package com.photo.app.api.users.ui.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.photo.app.api.users.service.UsersService;
import com.photo.app.api.users.shared.UserDto;
import com.photo.app.api.users.ui.model.CreateUserRequestModel;
import com.photo.app.api.users.ui.model.CreateUserResponseModel;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private Environment environment;

	@Autowired
	private UsersService usersService;

	@GetMapping("/status/check")
	public String status() {
		return "working on port = " + environment.getProperty("local.server.port") + ", with token = "
				+ environment.getProperty("token.secret");
	}

	@PostMapping
	public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userDetails) {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		UserDto createdUser = usersService.createUser(userDto);

		CreateUserResponseModel createUserResponseModel = modelMapper.map(createdUser, CreateUserResponseModel.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(createUserResponseModel);
	}
}