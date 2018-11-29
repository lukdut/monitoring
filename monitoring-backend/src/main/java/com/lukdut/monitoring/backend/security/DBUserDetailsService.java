package com.lukdut.monitoring.backend.security;

import com.lukdut.monitoring.backend.model.User;
import com.lukdut.monitoring.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DBUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    DBUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return new DBUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
