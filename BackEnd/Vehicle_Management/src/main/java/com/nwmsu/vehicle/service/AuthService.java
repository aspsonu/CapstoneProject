package com.nwmsu.vehicle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nwmsu.vehicle.entity.User;
import com.nwmsu.vehicle.repository.UserRepo;
import com.nwmsu.vehicle.utility.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /*public Map<String, Object> login(String userId, String password) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials! User not found.");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials! Incorrect password.");
        }

        Map<String, Object> response = new HashMap<>();

        if (user.isFirstTimeLogin()) {
            response.put("firstTimeLogin", true);
            response.put("message", "First-time login. Password change required.");
        } else {
            response.put("firstTimeLogin", false);
            String token = jwtUtil.generateToken(userId);
            response.put("token", token);
            response.put("role", user.getRole());
        }
        
        return response;
    } */
    
    public Map<String, Object> login(String userId, String password) {
        Optional<User> userOpt = userRepository.findByUserIdAndDeletedFalse(userId); // ✅ Updated

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials or account has been disabled.");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials! Incorrect password.");
        }

        Map<String, Object> response = new HashMap<>();

        if (user.isFirstTimeLogin()) {
            response.put("firstTimeLogin", true);
            response.put("message", "First-time login. Password change required.");
        } else {
            response.put("firstTimeLogin", false);
            String token = jwtUtil.generateToken(userId);
            response.put("token", token);
            response.put("role", user.getRole());
        }

        return response;
    }

}

