package org.example.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(sessionManagement ->
//                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.POST, "api/clients")
//                        .permitAll()
//                        .requestMatchers("api/login").permitAll()
//                        .anyRequest().authenticated())
//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
}
