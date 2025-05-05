package com.nwmsu.vehicle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nwmsu.vehicle.entity.Vehicle;

public interface VehicleRepo extends JpaRepository<Vehicle, Long> {
	
    Optional<Vehicle> findByVehicleIdentificationNumber(String vehicleIdentificationNumber);
    
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
    
    @Query("SELECT SUM(f.currentMileage) FROM FuelingEvent f WHERE YEAR(f.date) = :year AND MONTH(f.date) = :month")
    Integer getTotalMilesDriven(@Param("year") int year, @Param("month") int month);

    @Query(value = "SELECT DATE_FORMAT(v.date, '%Y-%m-%d') as mileageDate " +
            "FROM fueling_events v " +
            "WHERE YEAR(v.date) = :year AND MONTH(v.date) = :month " +
            "GROUP BY mileageDate " +
            "ORDER BY mileageDate ASC", nativeQuery = true)
    List<Object[]> getMilesDrivenGroupedByDate(@Param("year") int year, @Param("month") int month);

}

