package com.nwmsu.vehicle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nwmsu.vehicle.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByUserIdAndDeletedFalse(String userId); // ✅ NEW
    List<User> findAllByDeletedFalse(); // ✅ Optional: for admin list
    List<User> findAll();
}

