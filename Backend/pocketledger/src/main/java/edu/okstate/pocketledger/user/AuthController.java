package edu.okstate.pocketledger.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


// endpoints for registering and logging in
@RestController
@RequestMapping("/api/auth")

public class AuthController {
    
    private final UserService userService;

    // spring provides the service
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/auth/register (returns 201)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody CreateUserRequest request) {
        User saved = userService.createUser(request);
        return new UserResponse(saved.getId(), saved.getEmail());
    }

}
