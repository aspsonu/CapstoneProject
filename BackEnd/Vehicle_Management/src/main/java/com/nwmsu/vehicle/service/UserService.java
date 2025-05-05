package com.nwmsu.vehicle.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nwmsu.vehicle.dto.UserProfileDTO;
import com.nwmsu.vehicle.entity.User;
import com.nwmsu.vehicle.repository.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void firstTimeLogin(String userId, String currentPassword, String newPassword, String confirmPassword, Map<String, String> securityQuestions) {

        logger.info("Password change request received for user: {}", userId);

        Optional<User> userOpt = userRepository.findByUserId(userId);

        if (userOpt.isEmpty()) {
            logger.warn("User not found: {}", userId);
            throw new RuntimeException("User not found!");
        }

        User user = userOpt.get();

        if (!user.isFirstTimeLogin()) {
            throw new RuntimeException("User has already set up their password.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect current password.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New passwords do not match.");
        }

        // ✅ Ensure security questions are mapped properly
        if (securityQuestions == null || securityQuestions.size() != 2) {
            throw new RuntimeException("Exactly 2 security questions must be provided.");
        }

        for (Map.Entry<String, String> entry : securityQuestions.entrySet()) {
            String question = entry.getKey();
            String answer = entry.getValue();

            if (question == null || question.trim().isEmpty() ||
                answer == null || answer.trim().isEmpty()) {
                throw new RuntimeException("Security questions and answers must not be empty.");
            }
        }


        user.setPassword(passwordEncoder.encode(newPassword)); 
        // ✅ Assign questions and answers in a structured manner
        user.setSecurityQA(new LinkedHashMap<>(securityQuestions));
        user.setFirstTimeLogin(false); // Mark first-time login as completed

        userRepository.save(user);

        logger.info("First-time login completed successfully for user: {}", userId);
    }


    
    public void forgotPassword(String userId, Map<String, String> securityAnswers, String newPassword, String confirmPassword) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOpt.get();

        // Validate security answers
        if (!validateSecurityAnswers(user, securityAnswers)) {
            throw new RuntimeException("Security answers do not match.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public boolean verifySecurityAnswers(String userId, Map<String, String> providedAnswers) {
        Optional<User> userOpt = userRepository.findByUserId(userId);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return validateSecurityAnswers(user, providedAnswers);
    }
    
    private boolean validateSecurityAnswers(User user, Map<String, String> providedAnswers) {
        Map<String, String> storedQA = user.getSecurityQA();

        if (storedQA.size() != providedAnswers.size()) return false;

        for (Map.Entry<String, String> entry : storedQA.entrySet()) {
            String expectedAnswer = entry.getValue();
            String providedAnswer = providedAnswers.get(entry.getKey());

            if (providedAnswer == null || !expectedAnswer.equalsIgnoreCase(providedAnswer.trim())) {
                return false;
            }
        }
        return true;
    }

    
    public void changePassword(String userId, String currentPassword, String newPassword, String confirmPassword) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect current password.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
 // Get User Profile
    public UserProfileDTO getUserProfile(String userId) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with userId: " + userId);
        }

        User user = userOpt.get();
        return new UserProfileDTO(user.getFullName(), user.getEmail());
    }

    // Update User Profile
    public void updateUserProfile(String userId, UserProfileDTO userProfileDTO) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with userId: " + userId);
        }

        User user = userOpt.get();
        user.setFullName(userProfileDTO.getFullName());
        user.setEmail(userProfileDTO.getEmail());

        userRepository.save(user);
    }
    
    public Map<String, Object> getSecurityQuestions(String userId) {
        Optional<User> userOpt = userRepository.findByUserId(userId);

        if (userOpt.isEmpty()) {
            return Map.of();
        }

        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();

        // 🔥 Return only the questions (keys)
        response.put("questions", new ArrayList<>(user.getSecurityQA().keySet()));

        return response;
    }



}
