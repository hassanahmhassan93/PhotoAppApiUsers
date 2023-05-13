package com.photo.app.api.users.ui.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateUserRequestModel {

	@NotNull(message = "First Name cannot be Null")
	@Size(min = 2, message = "First Name must not be less than two characters")
	private String firstName;

	@NotNull(message = "Last Name cannot be Null")
	@Size(min = 2, message = "Last Name must not be less than two characters")
	private String lastName;

	@NotNull(message = "Password cannot be Null")
	@Size(min = 8, max = 16, message = "Password must be equal or grater than 8 characters and less than 16 characters")
	private String password;

	@NotNull(message = "Email cannot be Null")
	@Email
	private String email;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
