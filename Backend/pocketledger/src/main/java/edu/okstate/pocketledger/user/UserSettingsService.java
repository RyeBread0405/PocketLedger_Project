package edu.okstate.pocketledger.user;

import java.util.Currency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
public class UserSettingsService{
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserSettingsService(UserRepository userRepository, ObjectMapper objectMapper){
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public CurrencySettingsResponse getSettings(Integer userId){
        User user = findUser(userId);

        return new CurrencySettingsResponse(readSettings(user).path("currency").asText("USD"));
    }

    @Transactional
    public CurrencySettingsResponse updateSettings(Integer userId, CurrencySettingsRequest request){
        String currency = request.currency();

        // Validate the currency code
        try{
            Currency.getInstance(currency);
        } catch (IllegalArgumentException | NullPointerException e){
            throw new IllegalArgumentException("Invalid currency code");
        }

        User user = findUser(userId);

        ObjectNode settings = readSettings(user);

        settings.put("currency", currency);

        user.setSettingsJson(settings.toString());
        userRepository.save(user);

        return new CurrencySettingsResponse(currency);
    }

    private User findUser(Integer userId){
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private ObjectNode readSettings(User user){
        try{
            JsonNode node = objectMapper.readTree(user.getSettingsJson());

            if (node instanceof ObjectNode objectNode){
                return objectNode;
            }
            
            throw new IllegalStateException("User settings must be a JSON object");
        } catch (tools.jackson.core.JacksonException e){
            throw new IllegalStateException("Invalid user settings", e);
        }
    }
}
