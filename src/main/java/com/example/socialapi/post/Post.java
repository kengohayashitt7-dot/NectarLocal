package com.example.socialapi.post;

import com.example.socialapi.user.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Many posts belong to one author. LAZY = don't load the user unless asked. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User author;

    @Column(nullable = false, length = 280)
    private String content;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Post() {}

    public Post(User author, String content) {
        this.author = author;
        this.content = content;
    }

    public Long getId() { return id; }
    public User getAuthor() { return author; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
}
