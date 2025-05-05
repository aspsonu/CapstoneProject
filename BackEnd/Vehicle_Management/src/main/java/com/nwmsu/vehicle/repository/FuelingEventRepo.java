package com.nwmsu.vehicle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nwmsu.vehicle.entity.FuelingEvent;
import com.nwmsu.vehicle.entity.User;
import com.nwmsu.vehicle.entity.Vehicle;
import java.time.LocalDate;

public interface FuelingEventRepo extends JpaRepository<FuelingEvent, Long> {
	
    List<FuelingEvent> findAllByOrderByDateDesc();
    
    @Query("SELECT SUM(f.fuelCost) FROM FuelingEvent f WHERE YEAR(f.date) = :year AND MONTH(f.date) = :month")
    Double getTotalFuelingExpense(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(f.currentMileage) FROM FuelingEvent f WHERE f.vehicle.id = :vehicleId")
    Double getTotalMilesDrivenByVehicle(@Param("vehicleId") Long vehicleId);

    @Query("SELECT SUM(f.fuelAdded) FROM FuelingEvent f WHERE f.vehicle.id = :vehicleId")
    Double getTotalFuelAddedByVehicle(@Param("vehicleId") Long vehicleId);

    Optional<FuelingEvent> findTopByVehicleOrderByDateDesc(Vehicle vehicle);

    // User: Get fueling events created by a specific user
    List<FuelingEvent> findByCreatedByOrderByDateDesc(User createdBy);

    // Check if an event exists and belongs to the user (for update)
    //@Query("SELECT f FROM FuelingEvent f WHERE f.id = :eventId AND f.createdBy.id = :userId")
    //FuelingEvent findByIdAndCreatedBy(@Param("eventId") Long eventId, @Param("userId") String userId);
    
    @Query("SELECT f FROM FuelingEvent f WHERE f.id = :eventId AND f.createdBy.userId = :userId")
    FuelingEvent findByIdAndCreatedBy(@Param("eventId") Long eventId, @Param("userId") String userId);
    
    // ✅ Get Latest Mileage for a Vehicle
    @Query("SELECT fe.currentMileage FROM FuelingEvent fe WHERE fe.vehicle.id = :vehicleId ORDER BY fe.date DESC LIMIT 1")
    Double getLatestMileageByVehicle(@Param("vehicleId") Long vehicleId);

    @Query(value = "SELECT DATE_FORMAT(f.date, '%Y-%m-%d') as expenseDate, SUM(f.fuel_Cost) " +
            "FROM fueling_events f " +
            "WHERE YEAR(f.date) = :year AND MONTH(f.date) = :month " +
            "GROUP BY expenseDate " +
            "ORDER BY expenseDate ASC", nativeQuery = true)
    List<Object[]> getFuelExpenseGroupedByDate(@Param("year") int year, @Param("month") int month);

    List<FuelingEvent> findByVehicle(Vehicle vehicle);

    List<FuelingEvent> findByVehicleAndDateBetween(Vehicle vehicle, LocalDate start, LocalDate end);
    
    List<FuelingEvent> findByCreatedBy(User user);

}

