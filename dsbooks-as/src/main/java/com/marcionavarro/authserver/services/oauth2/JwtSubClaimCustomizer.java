package com.marcionavarro.authserver.services.oauth2;

import com.marcionavarro.authserver.entities.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
public class JwtSubClaimCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        Authentication principal = context.getPrincipal();
        Object principalObj = principal.getPrincipal();

        if (principalObj instanceof UserEntity user) {
            context.getClaims().subject(user.getSub());
        }

    }
}
