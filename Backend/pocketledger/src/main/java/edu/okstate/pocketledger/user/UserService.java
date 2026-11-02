package edu.okstate.pocketledger.user;

import org.springframework.stereotype.Service;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

//Service for actually registering a new user
@Service
public class UserService{

    private static final int MAX_ATTEMPTS = 100;
    private static final Duration WINDOW = Duration.ofHours(1);

    private final UserRepository userRepository; //User repository variable
    private final PasswordEncoder encoder; //Password encoder variable
    private final FailedAttemptRepository failedAttempts; // 


    //Constructor to provide the value for userRepository and the password encoder
    public UserService(UserRepository userRepository, PasswordEncoder encoder, FailedAttemptRepository failedAttempts){
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.failedAttempts = failedAttempts;
    }

    //Create a new User
    public User createUser(CreateUserRequest request){

        //Check if the email exists
        if (userRepository.findByEmail(request.email()).isPresent()){
            //Throw error. User already exists
            throw new IllegalArgumentException("Email is Already Registered");
        }

        //Hash the password
        String passwordHash = encoder.encode(request.password());

        //Create the new user
        User user = new User(request.email(), passwordHash);

        //Save the user to the database and return it
        return userRepository.save(user);
    }

    // find user, check password, hand them back
    public User login(LoginRequest request) {
        LocalDateTime cutoff = LocalDateTime.now().minus(WINDOW);

        // locked out if too many recent failures for an email
        if (failedAttempts.countByEmailAndLogtimeAfter(request.email(), cutoff) >= MAX_ATTEMPTS) {
            throw new TooManyAttemptsException();
        }

        Optional<User> found = userRepository.findByEmail(request.email());

        // same exception for unknown email and wrong password
        if (found.isEmpty() || !encoder.matches(request.password(), found.get().getPasswordHash())) {
            failedAttempts.save(new FailedAttempt(request.email(), LocalDateTime.now()));
            throw new InvalidCredentialsException();
        }
        
        return found.get();
    }
}