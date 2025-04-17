package com.cryptoportfolio.postgressDb.models;

import com.cryptoportfolio.postgressDb.dao.UserDao;
import com.cryptoportfolio.exceptions.LoginException;
import com.cryptoportfolio.exceptions.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired  // Spring will inject UserDao here
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Create user
    public void createUser(User user) {
        userDao.findByUsername(user.getUsername())
                .ifPresent(existingUser -> {
                    throw new UserAlreadyExistsException("User already exists!");
                });
        userDao.save(user); // Save new user
    }

    // Update user
    public void updateUser(User user) {
        User existingUser = userDao.findByUsername(user.getUsername())
                .orElseThrow(() -> new LoginException("User does not exist!"));
        existingUser.setIsNewUser(false);
        userDao.save(existingUser); // Save updated user
    }

    // Get user by username
    public User getUser(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(() -> new LoginException("User does not exist!"));
    }
}
