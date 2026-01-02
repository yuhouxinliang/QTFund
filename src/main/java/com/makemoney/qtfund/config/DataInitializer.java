package com.makemoney.qtfund.config;

import com.makemoney.qtfund.entity.User;
import com.makemoney.qtfund.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("qtfund").isEmpty()) {
                User user = new User();
                user.setUsername("qtfund");
                user.setPassword(passwordEncoder.encode("T*wNC!UX8TDGu3fx"));
                user.setRoles(Collections.singletonList("ADMIN"));
                userRepository.save(user);
                System.out.println("Initialized admin user: qtfund");
            }
        };
    }
}

