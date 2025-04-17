package com.cryptoportfolio.activity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cryptoportfolio.exceptions.LoginException;
import com.cryptoportfolio.models.requests.LoginRequest;
import com.cryptoportfolio.models.responses.LoginResponse;
import com.cryptoportfolio.postgressDb.models.UserService;
import com.cryptoportfolio.postgressDb.models.User;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginActivity {

    // Length of time auth token will remain valid in milliseconds
    private static final int TOKEN_DURATION = 3_600_000;

    private final UserService userService;
    private final Gson gson;

    // Constructor-based injection by Spring
    public LoginActivity(UserService userService, Gson gson) {
        this.userService = userService;
        this.gson = gson;
    }

    public LoginResponse execute(LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Validate input
        if (null == username || username.isEmpty() || null == password || password.isEmpty()) {
            throw new LoginException("Login Failed: username and password required");
        }

        // Get user from UserService (which internally uses UserDao)
        User user = userService.getUser(username);
        boolean isNewUser = user.getIsNewUser();

        if (user.getBlockedUser() != null) {
            throw new LoginException("Login Failed: user already blocked");
        }

        // Check if the provided password matches the stored password
        if (!password.equals(user.getPassword())) {
            throw new LoginException("Login Failed: incorrect password");
        }

        // Generate JWT token
        Date expiry = new Date();
        expiry.setTime(expiry.getTime() + TOKEN_DURATION);
        Algorithm algorithm = Algorithm.HMAC256(System.getenv("JWT_SECRET"));
        String token = JWT.create()
                .withIssuer("cryptoportfolio")
                .withClaim("username", username)
                .withExpiresAt(expiry)
                .sign(algorithm);

        // Build the login response
        return LoginResponse.builder()
                .username(username)
                .isAdminRight(user.isAdminRight())
                .isMainAdmin(user.isMainAdmin())
                .authToken(token)
                .isNewUser(isNewUser)
                .build();
    }
}
