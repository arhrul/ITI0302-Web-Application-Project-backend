package org.example.backend.service.client;

import jakarta.transaction.Transactional;
import lombok.Data;
import org.example.backend.model.Client;
import org.example.backend.model.Privilege;
import org.example.backend.model.Role;
import org.example.backend.repository.client.ClientRepository;
import org.example.backend.repository.auth.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("clientDetailsService")
@Transactional
@Data
public class ClientDetailsService implements UserDetailsService {
    private ClientRepository clientRepository;

    private ClientService service;

    private MessageSource messages;

    private RoleRepository roleRepository;

    @Value("${user.default.password}")
    private String defaultPassword;

    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Client client = clientRepository.findByEmail(email);
        if (client == null) {
            String encodedPassword = passwordEncoder.encode(defaultPassword);
            return new org.springframework.security.core.userdetails.User(
                    " ", encodedPassword, true, true, true, true,
                    getAuthorities(List.of(
                            roleRepository.findByName("ROLE_USER"))));
        }

        return new org.springframework.security.core.userdetails.User(
                client.getEmail(), client.getPassword(), true, true, true,
                true, getAuthorities(client.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(
            Collection<Role> roles) {

        return getGrantedAuthorities(getPrivileges(roles));
    }

    public List<String> getPrivileges(Collection<Role> roles) {

        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    public List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }
}
