package com.cryptoportfolio.activity;

import com.cryptoportfolio.exceptions.MissingFieldException;
import com.cryptoportfolio.models.requests.RegisterRequest;
import com.cryptoportfolio.models.responses.RegisterResponse;
import com.cryptoportfolio.postgressDb.models.UserService;
import com.cryptoportfolio.postgressDb.models.User;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class RegisterActivity {

    private final UserService userService;
    private final Gson gson;

    // Patterns for username and password validation
    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,20}$");
    private static final Pattern VALID_PASSWORD_PATTERN = Pattern.compile("^\\w{5,20}$");

    // Constructor-based injection by Spring
    public RegisterActivity(UserService userService, Gson gson) {
        this.userService = userService;
        this.gson = gson;
    }

    public RegisterResponse execute(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();

        // Validate input
        if (null == username || username.isEmpty() || null == password || password.isEmpty()) {
            throw new MissingFieldException("Registration Failed: Username and password are required");
        }

        // Validate username format
        if (!VALID_USERNAME_PATTERN.matcher(username).find()) {
            throw new IllegalArgumentException("Registration Failed: Username Valid format: (a-zA-Z0-9) & max of 20 characters.");
        }

        // Validate password format
        if (!VALID_PASSWORD_PATTERN.matcher(password).find()) {
            throw new IllegalArgumentException("Registration Failed: Password Valid format: 5 - 20 characters required.");
        }

        // Create user object
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setIsNewUser(true);
        user.setAdminRight(false);
        user.setMainAdmin(false);

        // Delegate user creation to the UserService
        userService.createUser(user);

        // Return the registration response
        return RegisterResponse.builder()
                .username(username)
                .build();
    }
}
