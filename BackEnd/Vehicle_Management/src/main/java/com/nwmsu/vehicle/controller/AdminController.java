package com.nwmsu.vehicle.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nwmsu.vehicle.dto.UserDTO;
import com.nwmsu.vehicle.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/create-user")
    public Map<String, String> createUser(@RequestBody UserDTO userDTO) {
        try {
            adminService.createUser(userDTO);
            return Map.of("message", "User created successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return adminService.getAllUsers();
    }
    
    @PutMapping("/update-user/{userId}")
    public Map<String, String> updateUser(@PathVariable("userId") String userId, @RequestBody UserDTO userDTO) {
        try {
            adminService.updateUser(userId, userDTO);
            return Map.of("message", "User updated successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @DeleteMapping("/delete-user/{userId}")
    public Map<String, String> deleteUser(@PathVariable("userId") String userId, Principal principal) {
        try {
            String requestingUserId = principal.getName(); // Get the logged-in admin's userId
            adminService.deleteUser(userId, requestingUserId);
            return Map.of("message", "User deleted successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    @PutMapping("/reactivate-user/{userId}")
    public Map<String, String> reactivateUser(@PathVariable("userId") String userId) {
        try {
            adminService.reactivateUser(userId);
            return Map.of("message", "User reactivated successfully.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }

}

