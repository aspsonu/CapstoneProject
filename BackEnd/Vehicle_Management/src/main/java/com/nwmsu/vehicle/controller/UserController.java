package com.nwmsu.vehicle.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nwmsu.vehicle.dto.ChangePasswordDTO;
import com.nwmsu.vehicle.dto.UserProfileDTO;
import com.nwmsu.vehicle.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Get User Profile (Admin & User)
    @GetMapping("/profile/{userId}")
    public UserProfileDTO getUserProfile(@PathVariable("userId") String userId) {
        return userService.getUserProfile(userId);
    }

    // Update User Profile (Admin & User)
    @PutMapping("/profile/update/{userId}")
    public Map<String, String> updateUserProfile(
            @PathVariable("userId") String userId,
            @RequestBody UserProfileDTO userProfileDTO) {
        try {
            userService.updateUserProfile(userId, userProfileDTO);
            return Map.of("message", "Profile updated successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }

    // Change User Password (Admin & User)
    @PutMapping("/profile/change-password/{userId}")
    public Map<String, String> changePassword(
            @PathVariable("userId") String userId,
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            userService.changePassword(userId, 
                                       changePasswordDTO.getCurrentPassword(), 
                                       changePasswordDTO.getNewPassword(), 
                                       changePasswordDTO.getConfirmPassword());
            return Map.of("message", "Password changed successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }

}

