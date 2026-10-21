package edu.okstate.pocketledger.user;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

// creates the signed token we hand back after login
@Service
public class TokenService {
    
    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String createToken(User user) {
        Instant now = Instant.now();

        // what token says
        // who it's for, who made it, when it expires
        JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("pocketledger")
        .issuedAt(now)
        .expiresAt(now.plus(24, ChronoUnit.HOURS))
        .subject(String.valueOf(user.getId()))
        .claim("email", user.getEmail())
        .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue(); 
    }
}
