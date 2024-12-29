package org.example.backend.test.service.client;

import org.example.backend.model.Client;
import org.example.backend.model.Privilege;
import org.example.backend.model.Role;
import org.example.backend.repository.client.ClientRepository;
import org.example.backend.service.client.ClientDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientDetailsServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientDetailsService clientDetailsService;

    private static final String TEST_EMAIL = "test@example.com";

    private Client testClient;
    private Role userRole;
    private Privilege readPrivilege;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        readPrivilege = new Privilege();
        readPrivilege.setName("READ_PRIVILEGE");

        userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setPrivileges(List.of(readPrivilege));

        testClient = new Client();
        testClient.setEmail(TEST_EMAIL);
        testClient.setPassword("encodedPassword");
        testClient.setRoles(List.of(userRole));
    }

    @Test
    void loadUserByUsername_ClientFound_ShouldReturnUserDetails() {
        when(clientRepository.findByEmail(TEST_EMAIL)).thenReturn(testClient);

        UserDetails userDetails = clientDetailsService.loadUserByUsername(TEST_EMAIL);

        assertNotNull(userDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("READ_PRIVILEGE")));
    }


    @Test
    void loadUserByUsername_ClientWithNoRoles_ShouldReturnUserDetailsWithEmptyAuthorities() {
        Client clientWithNoRoles = new Client();
        clientWithNoRoles.setEmail(TEST_EMAIL);
        clientWithNoRoles.setPassword("encodedPassword");
        clientWithNoRoles.setRoles(Collections.emptyList());

        when(clientRepository.findByEmail(TEST_EMAIL)).thenReturn(clientWithNoRoles);

        UserDetails userDetails = clientDetailsService.loadUserByUsername(TEST_EMAIL);

        assertNotNull(userDetails);
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void getPrivileges_WithRoles_ShouldReturnPrivilegesList() {
        List<String> privileges = clientDetailsService.getPrivileges(Collections.singletonList(userRole));

        assertEquals(2, privileges.size());
        assertTrue(privileges.contains("ROLE_USER"));
        assertTrue(privileges.contains("READ_PRIVILEGE"));
    }

    @Test
    void getGrantedAuthorities_WithPrivileges_ShouldReturnGrantedAuthorities() {
        List<GrantedAuthority> authorities = clientDetailsService.getGrantedAuthorities(List.of("ROLE_USER", "READ_PRIVILEGE"));

        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("READ_PRIVILEGE")));
    }
}
