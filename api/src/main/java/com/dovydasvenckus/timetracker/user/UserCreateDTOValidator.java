package com.dovydasvenckus.timetracker.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class UserCreateDTOValidator implements Validator {

//    private final UserService userService;
//
//    @Autowired
//    public UserCreateDTOValidator(UserService userService) {
//        this.userService = userService;
//    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserCreateDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserCreateDTO userDTO = (UserCreateDTO) target;
        validatePasswords(errors, userDTO);
//        validateEmail(errors, userDTO);
    }

    private void validatePasswords(Errors errors, UserCreateDTO userDTO) {
        if (!userDTO.getPassword().equals(userDTO.getPasswordRepeated())) {
            errors.reject("password.no_match", "Passwords do not match");
        }
    }

//    private void validateEmail(Errors errors, UserCreateDTO userDTO) {
//        if (userService.getUserByEmail(userDTO.getEmail()).isPresent()) {
//            errors.reject("email.exists", "User with this email already exists");
//        }
//    }
}

