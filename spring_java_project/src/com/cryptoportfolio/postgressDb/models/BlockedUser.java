package com.cryptoportfolio.postgressDb.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "blocked_users") // Separate table for blocked users
public class BlockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID for each record
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true) // Define username column
    private String username;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    private User user; // Reference to the User entity

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
