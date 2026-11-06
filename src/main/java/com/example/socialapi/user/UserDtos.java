package com.example.socialapi.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/** DTOs (request/response shapes) keep the API contract separate from the database entity. */
public final class UserDtos {
    private UserDtos() {}

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 30) String username,
            @NotBlank @Size(min = 8, max = 72) String password, // 72 = BCrypt's input limit
            @Size(max = 160) String bio) {}

    public record UserResponse(Long id, String username, String bio, Instant createdAt) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getUsername(), u.getBio(), u.getCreatedAt());
        }
    }
}
