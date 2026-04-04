package com.flowfund.engine.controller;

import com.flowfund.engine.entity.User;
import com.flowfund.engine.repository.UserRepository;
import com.flowfund.engine.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody com.flowfund.engine.dto.RegistrationRequest req) {
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setProvider("LOCAL");
        
        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("token", token);
        response.put("user", saved);
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String rawPassword = req.get("password");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return response;
    }
}
