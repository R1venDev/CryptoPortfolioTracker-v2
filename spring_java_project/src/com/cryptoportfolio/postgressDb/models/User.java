package com.cryptoportfolio.postgressDb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")  // Specify the table name here
public class User {

    @Id
    @Column(name = "username")
    private String username;

    @Column
    private String password;

    @Column(name = "is_new_user")
    private boolean isNewUser;

    @Column(name = "is_admin_right")
    private boolean isAdminRight;

    @Column(name = "is_main_admin")
    private boolean isMainAdmin;

    @JsonIgnore
    @OneToOne(mappedBy = "user") // mappedBy specifies the field in BlockedUser class that owns the relationship
    private BlockedUser blockedUser;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Portfolio> portfolios;

    public BlockedUser getBlockedUser() {
        return blockedUser;
    }

    public void setBlockedUser(BlockedUser blockedUser) {
        this.blockedUser = blockedUser;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }

    public boolean isAdminRight() {
        return isAdminRight;
    }

    public void setAdminRight(boolean adminRight) {
        isAdminRight = adminRight;
    }

    public boolean isMainAdmin() {
        return isMainAdmin;
    }

    public void setMainAdmin(boolean mainAdmin) {
        isMainAdmin = mainAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }
}
