package com.example.socialapi.user;

import com.example.socialapi.error.ConflictException;
import com.example.socialapi.error.NotFoundException;
import com.example.socialapi.user.UserDtos.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    // Constructor injection: Spring passes in the beans this class needs.
    public UserService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public User register(RegisterRequest req) {
        if (users.existsByUsername(req.username())) {
            throw new ConflictException("Username already taken");
        }
        return users.save(new User(req.username(), encoder.encode(req.password()), req.bio()));
    }

    public User get(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
