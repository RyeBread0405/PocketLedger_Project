package edu.okstate.pocketledger.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// reads and writes rows
// spring builds at startup
public interface UserRepository extends JpaRepository<User, Integer> {
    
    // spring reads method name and writes the sql
    // optional, might get a user or nobody

    //TO-DO: Revisit: this might also return multiple users, since email is not a primary key. See also: User.java
    Optional<User> findByEmail(String email);
}
