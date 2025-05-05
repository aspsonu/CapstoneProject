package com.nwmsu.vehicle.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nwmsu.vehicle.dto.UserDTO;
import com.nwmsu.vehicle.entity.Role;
import com.nwmsu.vehicle.entity.User;
import com.nwmsu.vehicle.repository.FuelingEventRepo;
import com.nwmsu.vehicle.repository.MaintenanceEventRepo;
import com.nwmsu.vehicle.repository.UserRepo;

@Service
public class AdminService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FuelingEventRepo fuelingEventRepo;

    @Autowired
    private MaintenanceEventRepo maintenanceEventRepo;

    public void createUser(UserDTO userDTO) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByUserId(userDTO.getUserId());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User ID already exists!");
        }

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        User newUser = User.builder()
                .userId(userDTO.getUserId())
                .fullName(userDTO.getFullName())
                .password(hashedPassword)
                .role(userDTO.getRole())
                .email(userDTO.getEmail())
                .firstTimeLogin(true) // First-time login set to true
                .securityQA(new LinkedHashMap<>())
                .build();

        userRepository.save(newUser);
    }
    
    public List<UserDTO> getAllUsers() {
        //List<User> users = userRepository.findAll();
        //List<User> users = userRepository.findAllByDeletedFalse();
        List<User> users = userRepository.findAll()
                .stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId())) // 👈 Sort by latest ID
                .collect(Collectors.toList());
        return users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setFullName(user.getFullName());
                    dto.setRole(user.getRole());
                    dto.setEmail(user.getEmail());
                    dto.setDeleted(user.isDeleted());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public void updateUser(String userId, UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOpt.get();
        
        // Update only non-sensitive fields
        user.setFullName(userDTO.getFullName());
        user.setRole(userDTO.getRole());
        user.setEmail(userDTO.getEmail());

        userRepository.save(user);
    }
    
    /*public void deleteUser(String userId, String requestingUserId) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        Optional<User> requestingUserOpt = userRepository.findByUserId(requestingUserId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        if (requestingUserOpt.isEmpty()) {
            throw new RuntimeException("Requesting user not found!");
        }

        User userToDelete = userOpt.get();
        User requestingUser = requestingUserOpt.get();
        
        // ✅ Prevent self-deletion
        if (userToDelete.getUserId().equals(requestingUser.getUserId())) {
            throw new RuntimeException("You cannot delete your own account!");
        }

        // Root-Admin cannot be deleted
        if (userToDelete.getRole().equals(Role.ROOT_ADMIN)) {
            throw new RuntimeException("Cannot delete Root-Admin user.");
        }

        // Regular Admin cannot delete Root-Admin
        if (requestingUser.getRole().equals(Role.ADMIN) && userToDelete.getRole().equals(Role.ROOT_ADMIN)) {
            throw new RuntimeException("Admins cannot delete Root-Admins.");
        }
        
     // ✅ Prevent Admin from deleting another Admin
        if (requestingUser.getRole().equals(Role.ADMIN) && userToDelete.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Admins cannot delete other Admins.");
        }

        //userRepository.delete(userToDelete);
        
        userToDelete.setDeleted(true);
        userRepository.save(userToDelete);

    } */
    
    public void deleteUser(String userId, String requestingUserId) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        Optional<User> requestingUserOpt = userRepository.findByUserId(requestingUserId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        if (requestingUserOpt.isEmpty()) {
            throw new RuntimeException("Requesting user not found!");
        }

        User userToDelete = userOpt.get();
        User requestingUser = requestingUserOpt.get();

        if (userToDelete.getUserId().equals(requestingUser.getUserId())) {
            throw new RuntimeException("You cannot delete your own account!");
        }

        if (userToDelete.getRole().equals(Role.ROOT_ADMIN)) {
            throw new RuntimeException("Cannot delete Root-Admin user.");
        }

        if (requestingUser.getRole().equals(Role.ADMIN) && userToDelete.getRole().equals(Role.ROOT_ADMIN)) {
            throw new RuntimeException("Admins cannot delete Root-Admins.");
        }

        if (requestingUser.getRole().equals(Role.ADMIN) && userToDelete.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Admins cannot delete other Admins.");
        }

        // ✅ Check for FK references
        boolean hasFuelingReferences = !fuelingEventRepo.findByCreatedBy(userToDelete).isEmpty();
        boolean hasMaintenanceReferences = !maintenanceEventRepo.findByCreatedBy(userToDelete).isEmpty();

        if (!hasFuelingReferences && !hasMaintenanceReferences) {
            userRepository.delete(userToDelete); // 🔥 Hard delete
        } else {
            userToDelete.setDeleted(true);       // 🚫 Soft delete
            userRepository.save(userToDelete);
        }
    }

    public void reactivateUser(String userId) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOpt.get();

        if (!user.isDeleted()) {
            throw new RuntimeException("User is already active.");
        }

        user.setDeleted(false);
        userRepository.save(user);
    }

}

