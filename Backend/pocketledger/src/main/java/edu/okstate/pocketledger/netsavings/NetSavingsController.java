package edu.okstate.pocketledger.netsavings;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/net-savings")
public class NetSavingsController{
    private final NetSavingsService netSavingsService;

    public NetSavingsController(NetSavingsService netSavingsService){
        this.netSavingsService = netSavingsService;
    }

    @GetMapping
    public NetSavingsResponse getNetSavings(JwtAuthenticationToken authentication){
        Integer userId = Integer.valueOf(authentication.getToken().getSubject());

        return new NetSavingsResponse(netSavingsService.calculateNetSavings(userId));
    }
}