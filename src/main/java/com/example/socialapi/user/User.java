package com.example.socialapi.user;

import jakarta.persistence.*;
import java.time.Instant;

/** A JPA entity: one row in the "users" table. ("user" is a reserved word in some databases.) */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    /** BCrypt hash, never the raw password. */
    @Column(nullable = false)
    private String passwordHash;

    private String bio;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected User() {} // required by JPA

    public User(String username, String passwordHash, String bio) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.bio = bio;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getBio() { return bio; }
    public Instant getCreatedAt() { return createdAt; }
}
