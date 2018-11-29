package com.lukdut.monitoring.backend.security;

import com.lukdut.monitoring.backend.model.User;
import com.lukdut.monitoring.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.lukdut.monitoring.backend.security.Roles.ROLE_PREFIX;

@Service
public class FirstRunUserAddService {
    private static final Logger LOG = LoggerFactory.getLogger(FirstRunUserAddService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    FirstRunUserAddService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void addAdminUser() {
        Boolean isFirstRun = Boolean.valueOf(System.getProperty("firstRun", "false"));
        if (isFirstRun) {
            LOG.info("First time starting, creating admin user");
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRole(ROLE_PREFIX+Roles.ADMIN.name());
            userRepository.save(user);
        }
    }
}
