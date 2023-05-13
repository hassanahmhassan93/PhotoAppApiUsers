package com.photo.app.api.users.ui.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginRequestModel {

	@NotNull(message = "Email cannot be Null")
	@Email
	private String email;
	
	@NotNull(message = "Password cannot be Null")
	@Size(min = 8, max = 16, message = "Password must be equal or grater than 8 characters and less than 16 characters")
	private String password;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
