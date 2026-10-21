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
    private final TokenService tokenService;

    // spring provides the service
    public AuthController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    // POST /api/auth/register (returns 201)
    // returns token too so user is logged in right after registering
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody CreateUserRequest request) {
        User saved = userService.createUser(request);
        return new AuthResponse(tokenService.createToken(saved), new UserResponse(saved.getId(), saved.getEmail()));
    }


    // POST /api/auth/login (returns 200)
    // checks password then hands back same stuff as register
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request);
        return new AuthResponse(tokenService.createToken(user), new UserResponse(user.getId(), user.getEmail()));
    }

}
