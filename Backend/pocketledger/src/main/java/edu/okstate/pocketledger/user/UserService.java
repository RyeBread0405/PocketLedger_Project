package edu.okstate.pocketledger.user;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

//Service for actually registering a new user
@Service
public class UserService{

    private final UserRepository userRepository; //User repository variable
    private final PasswordEncoder encoder; //Password encoder variable


    //Constructor to provide the value for userRepository and the password encoder
    public UserService(UserRepository userRepository, PasswordEncoder encoder){
        this.userRepository = userRepository;
        this.encoder = encoder;
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
}