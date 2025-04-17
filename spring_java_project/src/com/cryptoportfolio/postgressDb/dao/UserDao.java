package com.cryptoportfolio.postgressDb.dao;

import com.cryptoportfolio.postgressDb.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;


@Repository
public interface UserDao extends JpaRepository<User, String> {

    // Custom query method to find a user by username
    Optional<User> findByUsername(String username);

    // Custom query method to delete a user by username
    void deleteByUsername(String username);

    // Custom query method to update the isAdminRight to true by username
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isAdminRight = true WHERE u.username = :username")
    int updateAdminRights(@Param("username") String username);
}
