//Test to make sure correct currency is returned from the database for a user
package edu.okstate.pocketledger.user;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;

public class UserSettingsServiceTest{
    @Test
    void getSettingsReturnsUSD(){

        //Fake user
        User user = new User("test@test.com", "fakePassowrdHash");

        //Fake repository
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        //When we call findById with 1, return the fake user
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        //Create the service with the fake repository
        UserSettingsService service = new UserSettingsService(userRepository, new ObjectMapper());

        CurrencySettingsResponse response = service.getSettings(1);
        
        assertEquals("USD", response.currency());
    }

    @Test
    void updateSettingsChangesCurrency() {

        // Create a fake user with default USD settings
        User user = new User("test@test.com", "fakePasswordHash");

        // Create a fake repository
        UserRepository userRepository = Mockito.mock(UserRepository.class);

        // Tell the repository to return our fake user
        when(userRepository.findById(1))
            .thenReturn(Optional.of(user));

        // Create the service
        UserSettingsService service = new UserSettingsService(
            userRepository,
            new ObjectMapper()
        );

        // Create a request to change the currency to EUR
        CurrencySettingsRequest request = new CurrencySettingsRequest("EUR");

        // Call the method we're testing
        CurrencySettingsResponse response = service.updateSettings(1, request);

        // Check that the returned currency is EUR
        assertEquals("EUR", response.currency());

        // Check that the user's settings were actually updated
        assertEquals("{\"currency\":\"EUR\"}", user.getSettingsJson());
    }
}