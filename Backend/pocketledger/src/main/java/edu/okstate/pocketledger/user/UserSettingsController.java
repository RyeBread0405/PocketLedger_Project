package edu.okstate.pocketledger.user;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/settings")
public class UserSettingsController{
    private final UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService){
        this.userSettingsService = userSettingsService;
    }

    //Retrieve the current settings for the authenticated user
    @GetMapping
    public CurrencySettingsResponse getSettings (JwtAuthenticationToken authentication){
        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        return userSettingsService.getSettings(userId);
    }

    @PutMapping
    public CurrencySettingsResponse updateSettings(@Valid @RequestBody CurrencySettingsRequest request, JwtAuthenticationToken authentication){
        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        return userSettingsService.updateSettings(userId, request);
    }
}