package edu.okstate.pocketledger.ai;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;


//POST /api/ai/categorize
// needs token
@RestController 
@RequestMapping("/api/ai")
public class AiController {
    
    private final AiCategorizationService aiCategorizationService;

    public AiController(AiCategorizationService aiCategorizationService) {
        this.aiCategorizationService = aiCategorizationService;
    }

    @PostMapping("/categorize")
    public CategorizeResponse categorize(@Valid @RequestBody CategorizeRequest request, JwtAuthenticationToken authentication) {
        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        return aiCategorizationService.categorize(request, userId);
    }
}
