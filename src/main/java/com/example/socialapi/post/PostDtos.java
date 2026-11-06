package com.example.socialapi.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class PostDtos {
    private PostDtos() {}

    public record CreatePostRequest(@NotBlank @Size(max = 280) String content) {}

    public record PostResponse(Long id, Long authorId, String authorUsername, String content, Instant createdAt) {
        public static PostResponse from(Post p) {
            return new PostResponse(p.getId(), p.getAuthor().getId(), p.getAuthor().getUsername(),
                    p.getContent(), p.getCreatedAt());
        }
    }
}
