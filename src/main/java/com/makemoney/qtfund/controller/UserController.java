package com.makemoney.qtfund.controller;

import com.makemoney.qtfund.entity.User;
import com.makemoney.qtfund.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("username", user.getUsername());
            result.put("roles", user.getRoles());
        }
        return result;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        // Since we are secured by hasRole("ADMIN") in SecurityConfig, we can just return all.
        // Be careful not to expose passwords. Ideally use a DTO.
        List<User> users = userRepository.findAll();
        users.forEach(u -> u.setPassword(null)); // Clear password for safety
        return users;
    }

    @PostMapping("/users")
    public User createUser(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        String password = (String) payload.get("password");
        // expirationDate from frontend is likely ISO string or timestamp
        // Let's assume frontend sends timestamp (long) or handle ISO string conversion if needed.
        // Or simple simpler: receive days valid.
        // Let's assume frontend sends `expirationDate` as ISO string or timestamp.
        // Simpler for now: `validDays` (int)
        
        Integer validDays = (Integer) payload.get("validDays");
        
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList("USER")); // Default role
        
        if (validDays != null && validDays > 0) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.DAY_OF_YEAR, validDays);
            user.setExpirationDate(cal.getTime());
        }

        return userRepository.save(user);
    }
}
