package com.cryptoportfolio.controllers;

import com.cryptoportfolio.activity.*;
import com.cryptoportfolio.models.PortfolioModel;
import com.cryptoportfolio.models.requests.*;
import com.cryptoportfolio.models.responses.GetPortfolioResponse;
import com.cryptoportfolio.models.responses.LoginResponse;
import com.cryptoportfolio.models.responses.RegisterResponse;
import com.cryptoportfolio.postgressDb.dao.BlockedUserDao;
import com.cryptoportfolio.postgressDb.dao.TransactionDao;
import com.cryptoportfolio.postgressDb.dao.UserDao;
import com.cryptoportfolio.postgressDb.models.BlockedUser;
import com.cryptoportfolio.postgressDb.models.Portfolio;
import com.cryptoportfolio.postgressDb.dao.PortfolioDao;
import com.cryptoportfolio.converter.ModelConverter;
import com.cryptoportfolio.postgressDb.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class Controller {

    private final RegisterActivity registerActivity;
    private final LoginActivity loginActivity;
    private final GetPortfolioActivity getPortfolioActivity;
    private final CreatePortfolioActivity createPortfolioActivity;
    private final UpdatePortfolioActivity updatePortfolioActivity;
    private final GetTransactionsActivity getTransactionsActivity;
    private final PortfolioDao portfolioDao; // Injecting the PortfolioDao
    private final TransactionDao transactionDao;
    private final UserDao userDao;
    private final ModelConverter modelConverter; // Injecting the ModelConverter
    private BlockedUserDao blockedUserDao;

    @Autowired
    public Controller(RegisterActivity registerActivity,
                      LoginActivity loginActivity,
                      GetPortfolioActivity getPortfolioActivity,
                      CreatePortfolioActivity createPortfolioActivity,
                      UpdatePortfolioActivity updatePortfolioActivity,
                      GetTransactionsActivity getTransactionsActivity,
                      PortfolioDao portfolioDao,
                      TransactionDao transactionDao, ModelConverter modelConverter, UserDao userDao, BlockedUserDao blockedUserDao) {  // Adding ModelConverter to constructor
        this.registerActivity = registerActivity;
        this.loginActivity = loginActivity;
        this.getPortfolioActivity = getPortfolioActivity;
        this.createPortfolioActivity = createPortfolioActivity;
        this.updatePortfolioActivity = updatePortfolioActivity;
        this.getTransactionsActivity = getTransactionsActivity;
        this.portfolioDao = portfolioDao;  // Initialize portfolioDao
        this.transactionDao = transactionDao;
        this.modelConverter = modelConverter; // Initialize ModelConverter
        this.userDao = userDao;
        this.blockedUserDao = blockedUserDao;
    }

    @GetMapping(value = "api/get-users", produces = {"application/json"})
    public ResponseEntity<?> getUsers() {
        try {
            List<User> users = userDao.findAll().stream().filter(user -> user.getBlockedUser() == null).collect(Collectors.toList());

            // If no users are found, return an empty list (avoid error response after the response is committed)
            if (users.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);  // Return empty list, not an error message
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "api/get-blocked-users", produces = {"application/json"})
    public ResponseEntity<?> getBlockedUsers() {
        try {
            List<BlockedUser> blockedUsers = blockedUserDao.findAll();

            if (blockedUsers.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);  // Return empty list, not an error message
            }

            return new ResponseEntity<>(blockedUsers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving blocked users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @PostMapping(value = "/api/blockUser/{userName}", produces = {"application/json"})
    @Transactional
    public ResponseEntity<?> blockUser(@PathVariable String userName) {
        try {
            System.out.println("Blocking user: " + userName); // Log the username

            // Check if the user exists in the database
            User user = userDao.findByUsername(userName).orElse(null);
            if (user == null) {
                System.out.println("User not found: " + userName); // Log user not found
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }

            // Check if the user is already blocked
            if (blockedUserDao.existsByUsername(userName)) {
                System.out.println("User already blocked: " + userName); // Log if user is already blocked
                return new ResponseEntity<>("User is already blocked.", HttpStatus.BAD_REQUEST);
            }

            // Create a new BlockedUser entry
            BlockedUser blockedUserEntry = new BlockedUser();
            blockedUserEntry.setUser(user); // Associate the user
            blockedUserEntry.setUsername(user.getUsername()); // Set the username explicitly

            blockedUserDao.save(blockedUserEntry); // Save the blocked user entry

            System.out.println("User blocked successfully: " + userName); // Log success
            return new ResponseEntity<>("User blocked successfully.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full error stack trace
            return new ResponseEntity<>("An error occurred while blocking the user.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping(value = "/api/deleteUser/{userName}", produces = {"application/json"})
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable String userName) {
        try {
            // Check if the user is present in the blocked_users table
            if (blockedUserDao.existsByUsername(userName)) {
                blockedUserDao.deleteByUsername(userName);
            }

            Optional<User> user = userDao.findByUsername(userName);
            // Then, delete the user from the users table
            if (user.isPresent()) {
                for (Portfolio portfolio : user.get().getPortfolios()) {
                    portfolioDao.deleteByPortfolioId(portfolio.getPortfolioId());
                    transactionDao.deleteByPortfolioId(portfolio.getPortfolioId());
                }
                userDao.deleteByUsername(userName);  // Assuming you have a method to delete by username
                return new ResponseEntity<>("User and associated blocked record deleted successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while deleting the user.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Handle POST requests for /register
    @PostMapping(value = "api/register", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> registerActivity(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = registerActivity.execute(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Handle POST requests for /register
    @PutMapping(value = "/api/give-admin-rights/{userName}", produces = "application/json")
    public ResponseEntity<?> giveAdminRights(@PathVariable String userName) {
        try {
            Optional<User> userOpt = userDao.findByUsername(userName);
            if (userOpt.isPresent()) {
                userDao.updateAdminRights(userName);
                return new ResponseEntity<>("Admin rights granted successfully.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating user.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // Handle OPTIONS preflight requests for /register
    @RequestMapping(method = RequestMethod.OPTIONS, value = "/register")
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build(); // Respond with 200 OK for preflight request
    }

    // Handle POST requests for /login
    @PostMapping(value = "api/login", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> loginActivity(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = loginActivity.execute(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Handle GET requests for /portfolio/{username}
    @GetMapping(value = "api/get-portfolio/{username}", produces = {"application/json"})
    public ResponseEntity<?> getPortfolioActivity(@PathVariable String username,  // Accept username as path variable
            @RequestHeader("cp-auth-token") String authToken) {

        // Fetch portfolios for the user from the database using the PortfolioDao
        List<Portfolio> portfolios = portfolioDao.findByUsername(username); // Call the DAO method to get portfolios by username

        // Optionally, validate the authToken for the user (authentication and authorization check)
        if (!isValidAuthTokenForUser(authToken, username)) {
            return new ResponseEntity<>("Unauthorized access", HttpStatus.UNAUTHORIZED);
        }

        if (portfolios.isEmpty()) {
            return new ResponseEntity<>(new GetPortfolioResponse(), HttpStatus.OK);
        }

        // Convert portfolios to PortfolioModel objects
        List<PortfolioModel> portfolioModels = modelConverter.toPortfolioModelList(portfolios);
        if (portfolioModels.size() == 1) {
            return new ResponseEntity<>(new GetPortfolioResponse(portfolioModels.get(0)), HttpStatus.OK);
        }
        // Return portfolio response wrapped with the list of portfolio models
        return new ResponseEntity<>(new GetPortfolioResponse(portfolioModels), HttpStatus.OK);
    }


    // Handle POST requests for /portfolio/{id}
    @PostMapping(value = "api/portfolio", produces = {"application/json"})
    public ResponseEntity<?> createPortfolioActivity(@RequestHeader("cp-auth-token") String authToken,
                                                     @Valid @RequestBody CreatePortfolioRequest createRequest) {
        CreatePortfolioRequest createPortfolioRequest = CreatePortfolioRequest.builder()
                .username(createRequest.getUsername())
                .authToken(authToken)
                .assetQuantityMap(createRequest.getAssetQuantityMap())
                .transactions(createRequest.getTransactions())
                .build();
        return new ResponseEntity<>(createPortfolioActivity.execute(createPortfolioRequest), HttpStatus.OK);
    }

    // Handle PUT requests for /portfolio/{id}
    @PutMapping(value = "api/portfolio/{id}", produces = {"application/json"})
    public ResponseEntity<?> updatePortfolioActivity(@PathVariable Long id,
                                                     @RequestHeader("cp-auth-token") String authToken,
                                                     @Valid @RequestBody UpdatePortfolioRequest updateRequest) {
        UpdatePortfolioRequest updatePortfolioRequest = UpdatePortfolioRequest.builder()
                .id(id)
                .authToken(authToken)
                .assetQuantityMap(updateRequest.getAssetQuantityMap())
                .transactions(updateRequest.getTransactions())
                .build();
        return new ResponseEntity<>(updatePortfolioActivity.execute(updatePortfolioRequest), HttpStatus.OK);
    }

    // Handle GET requests for /transactions/{id}
    @GetMapping(value = "api/transactions/{id}", produces = {"application/json"})
    public ResponseEntity<?> getTransactionActivity(@PathVariable Long id,
                                                    @RequestParam String assetFlag,
                                                    @RequestHeader("cp-auth-token") String authToken) {
        GetTransactionsRequest getTransactionsRequest = GetTransactionsRequest.builder()
                .portfolioId(id)
                .authToken(authToken)
                .assetFlag(assetFlag)
                .build();
        return new ResponseEntity<>(getTransactionsActivity.execute(getTransactionsRequest), HttpStatus.OK);
    }

    @DeleteMapping(value = "api/portfolio/{id}", produces = {"application/json"})
    @Transactional
    public ResponseEntity<?> deletePortfolio(@PathVariable Integer id, @RequestHeader("cp-auth-token") String authToken) {
        // Find and delete the portfolio
        try {
            portfolioDao.deleteByPortfolioId(Long.valueOf(id));
            transactionDao.deleteByPortfolioId(Long.valueOf(id));
            return new ResponseEntity<>("Portfolio deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting portfolio: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Utility method to validate auth token for the user (example)
    private boolean isValidAuthTokenForUser(String authToken, String username) {
        // Logic to check if the token is valid for the user (could involve decoding the token and matching the username)
        return true; // Assume it's valid for this example
    }
}
