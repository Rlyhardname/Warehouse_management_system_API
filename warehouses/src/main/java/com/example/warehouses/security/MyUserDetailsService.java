package com.example.warehouses.security;

import com.example.warehouses.configurations.Enum.Role;
import com.example.warehouses.exception.Client.UserNotExististingException;
import com.example.warehouses.interfaces.Administrator;
import com.example.warehouses.model.user.UserImpl;
import com.example.warehouses.repository.AdminRepository;
import com.example.warehouses.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private UsersRepository usersRepository;
    private AdminRepository adminRepository;

    @Autowired
    public MyUserDetailsService(UsersRepository usersRepository,
                                AdminRepository adminRepository) {
        this.usersRepository = usersRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetailsClass loadUserByUsername(String email1) {
        String email = null;
        String password = null;
        String ROLE = null;
        Optional<Administrator> admin;
        Optional<UserImpl> client = usersRepository.findByEmail(email1);

        if (client.isPresent()) {
            email = client.get().getEmail();
            password = client.get().getPassword();
            if (client.get().getDType().equals("owner")) {
                ROLE = Role.OWNER.name();
            } else {
                ROLE = Role.AGENT.name();
            }
        } else {
            admin = adminRepository.findByEmail(email1);
            if (admin.isPresent() == true) {
                email = admin.get().getEmail();
                password = admin.get().getPassword();
                ROLE = Role.ADMIN.name();
            } else{
                throw new UserNotExististingException();
            }
        }

        SecurityProperties.User user = new SecurityProperties.User();

        Set<GrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map((role) -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());

        UserDetailsClass user1 = new UserDetailsClass(email, password, ROLE, authorities);

        return user1;

    }
}
