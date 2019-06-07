package com.psl.cc.analytics.validator;

import org.springframework.validation.Errors;

import com.psl.cc.analytics.response.User;

public class UserValidator implements org.springframework.validation.Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User) target;
		if (user.isUseAPIKey() && user.isUsePassword()) {
			errors.rejectValue("usePassword", "Please enable either useAPIKey or usePassword");
		}
		if (!user.isUseAPIKey() && !user.isUsePassword()) {
			errors.rejectValue("useAPIKey", "Please enable either useAPIKey or usePassword");
		}

	}

}
