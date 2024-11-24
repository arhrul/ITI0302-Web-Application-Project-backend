package org.example.backend.security;

import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.SecretKey;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {

    @Bean
    public SecretKey jwtKey() {
        return Jwts.SIG.HS256.key().build();
    }
}
