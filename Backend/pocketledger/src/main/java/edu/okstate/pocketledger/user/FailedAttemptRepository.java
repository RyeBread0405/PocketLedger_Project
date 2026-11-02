package edu.okstate.pocketledger.user;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedAttemptRepository extends JpaRepository<FailedAttempt, Long> {
    
    // spring builds query from method name
    long countByEmailAndLogtimeAfter(String email, LocalDateTime cutoff);
}


