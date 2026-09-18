package edu.okstate.pocketledger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


// health check for backend
// used by frontend to confirm the api is reachable
@RestController 
public class HealthController {
    
    // GET http://localhost:8080/api/health  =>  200 "OK"
    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
    
}
