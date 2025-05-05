package com.nwmsu.vehicle.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nwmsu.vehicle.entity.User;
import com.nwmsu.vehicle.repository.UserRepo;
import com.nwmsu.vehicle.service.AuthService;
import com.nwmsu.vehicle.service.UserService;
import com.nwmsu.vehicle.utility.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepo userRepository;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        try {
            return authService.login(request.get("userId"), request.get("password"));
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("message", "Logout successful");
    }
    
    @PostMapping("/first-time-login")
    public ResponseEntity<Map<String, String>> firstTimeLogin(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String currentPassword = (String) request.get("currentPassword");
            String newPassword = (String) request.get("newPassword");
            String confirmPassword = (String) request.get("confirmPassword");
            Map<String, String> securityQuestions = (Map<String, String>) request.get("securityQuestions");

            // ✅ Ensure all values are provided
            if (userId == null || currentPassword == null || newPassword == null || confirmPassword == null || securityQuestions == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "All fields are required!"));
            }

            userService.firstTimeLogin(userId, currentPassword, newPassword, confirmPassword, securityQuestions);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully. You can now log in."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    
    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("Received Payload: " + request); // ✅ Debugging Log

            userService.forgotPassword(
                (String) request.get("userId"),
                (Map<String, String>) request.get("securityAnswers"),
                (String) request.get("newPassword"),
                (String) request.get("confirmPassword")
            );
            return ResponseEntity.ok(Map.of("message", "Password reset successfully. You can now log in."));
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid data format."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public Map<String, String> changePassword(@RequestBody Map<String, String> request, Principal principal) {
        try {
            userService.changePassword(
                principal.getName(),
                request.get("currentPassword"),
                request.get("newPassword"),
                request.get("confirmPassword")
            );
            return Map.of("message", "Password updated successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (jwtUtil.validateToken(refreshToken)) {
            String userId = jwtUtil.extractUserId(refreshToken);
            String newToken = jwtUtil.generateToken(userId);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId); // ✅ Issue new refresh token
            return ResponseEntity.ok(Map.of("token", newToken, "refreshToken", newRefreshToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid refresh token"));
    }
    
    @PostMapping("/security-questions")
    public ResponseEntity<Map<String, Object>> getSecurityQuestions(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");

        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "User ID is required"));
        }

        Map<String, Object> securityQuestions = userService.getSecurityQuestions(userId);

        if (securityQuestions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found or no security questions set."));
        }

        return ResponseEntity.ok(securityQuestions);
    }



    @PostMapping("/verify-security-answers")
    public ResponseEntity<?> verifySecurityAnswers(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        Map<String, String> securityAnswers = (Map<String, String>) request.get("securityAnswers");

        if (userService.verifySecurityAnswers(userId, securityAnswers)) {
            return ResponseEntity.ok(Map.of("message", "Security answers verified."));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Security answers do not match."));
        }
    }


}

