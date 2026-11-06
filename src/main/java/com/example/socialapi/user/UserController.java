package com.example.socialapi.user;

import com.example.socialapi.user.UserDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    /** Public: create an account. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        return UserResponse.from(service.register(req));
    }

    /** The currently logged-in user. The JWT subject is the user id. */
    @GetMapping("/me")
    public UserResponse me(Authentication auth) {
        return UserResponse.from(service.get(Long.valueOf(auth.getName())));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return UserResponse.from(service.get(id));
    }
}
