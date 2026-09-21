package edu.okstate.pocketledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// starts PocketLedger backend
@SpringBootApplication
public class PocketledgerApplication {

	// entry point, boots spring
	public static void main(String[] args) {
		SpringApplication.run(PocketledgerApplication.class, args);
	}

}
