package com.nwmsu.vehicle.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vehicleNumber;

    @Column(nullable = false, unique = true)
    private String vehicleIdentificationNumber;

    @Column(nullable = false)
    private int modelYear;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String purchaseDate;

    @Column(nullable = false)
    private int startingMileage;

    @Column(nullable = false)
    private String vehicleWeight;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private String vehicleDescription;

    @Column(nullable = false)
    private boolean lawEnforcement;

    @Column(nullable = false)
    private boolean exemptType;
    
    @Column(nullable = false)
    private boolean deleted = false;
}

