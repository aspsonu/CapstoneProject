package com.nwmsu.vehicle.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nwmsu.vehicle.entity.Notification;
import com.nwmsu.vehicle.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable("userId") String userId) {
        return notificationService.getUserNotifications(userId);
    }

    @PutMapping("/mark-as-read/{notificationId}")
    public Map<String, String> markNotificationAsRead(@PathVariable("notificationId") Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return Map.of("message", "Notification marked as read.");
        } catch (RuntimeException e) {
            return Map.of("error", e.getMessage());
        }
    }
}
