package com.taskify.security;

import com.taskify.user.models.UserModel;
import com.taskify.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = this.userRepository.findByEmail(email).orElse(null);
        System.out.println("User: " + user);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        // Create UserDetails object using user data
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER") // Add user roles if needed
                .build();
    }

}
