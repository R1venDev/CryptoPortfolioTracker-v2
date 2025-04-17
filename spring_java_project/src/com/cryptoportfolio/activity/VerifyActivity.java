package com.cryptoportfolio.activity;

import com.cryptoportfolio.models.requests.VerifyRequest;
import com.cryptoportfolio.models.responses.VerifyResponse;
import com.cryptoportfolio.postgressDb.dao.UserDao;
import com.cryptoportfolio.utils.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerifyActivity {

    private final UserDao userDao;

    @Autowired
    public VerifyActivity(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Handles the incoming request to verify a user's authentication token.
     *
     * @param verifyRequest Request object containing the username and auth token
     * @return VerifyResponse Response object with the username and token if verified successfully
     */
    public VerifyResponse execute(VerifyRequest verifyRequest) {
        String username = verifyRequest.getUsername();
        String token = verifyRequest.getAuthToken();

        Auth.authenticateToken(username, token);

        return VerifyResponse.builder()
                .username(username)
                .authToken(token)
                .build();
    }
}
