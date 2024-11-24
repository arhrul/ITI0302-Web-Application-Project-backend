package org.example.backend.controller;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.example.backend.dtos.ClientDTO;
import org.example.backend.model.Client;
import org.example.backend.repository.ClientRepository;
import org.example.backend.security.ApplicationConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();

    @PostMapping
    public ResponseEntity<String> login(@RequestBody ClientDTO clientDTO) {
        Client client = clientRepository.findById(clientDTO.getId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        if (!passwordEncoder.matches(clientDTO.getPassword(), client.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        String token = generateToken(client);
        return ResponseEntity.ok(token);
    }

    private String generateToken(Client client) {
        return Jwts.builder().subject(client.getEmail())
                .claims(Map.of("id", client.getId()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(applicationConfiguration.jwtKey())
                .compact();
    }
}
