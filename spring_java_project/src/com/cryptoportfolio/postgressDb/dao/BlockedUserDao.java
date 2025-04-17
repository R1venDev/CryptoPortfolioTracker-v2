package com.cryptoportfolio.postgressDb.dao;

import com.cryptoportfolio.postgressDb.models.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedUserDao extends JpaRepository<BlockedUser, String> {

    // Custom query method to find a user by username
    Optional<BlockedUser> findByUsername(String username);

    // Custom query method to delete a user by username
    void deleteByUsername(String username);

    boolean existsByUsername(String username);
}