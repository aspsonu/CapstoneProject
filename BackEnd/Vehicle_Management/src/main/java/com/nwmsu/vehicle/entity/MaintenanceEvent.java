package com.nwmsu.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double maintenanceCost;

    @Column(nullable = false, length = 255)
    private String maintenanceDescription;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Associate event with a user
    private User createdBy;
}
