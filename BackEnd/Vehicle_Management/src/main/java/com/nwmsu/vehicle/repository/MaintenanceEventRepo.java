package com.nwmsu.vehicle.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nwmsu.vehicle.entity.MaintenanceEvent;
import com.nwmsu.vehicle.entity.User;
import com.nwmsu.vehicle.entity.Vehicle;

public interface MaintenanceEventRepo extends JpaRepository<MaintenanceEvent, Long> {
	
    List<MaintenanceEvent> findAllByOrderByDateDesc();
    
    @Query("SELECT SUM(m.maintenanceCost) FROM MaintenanceEvent m WHERE YEAR(m.date) = :year AND MONTH(m.date) = :month")
    Double getTotalMaintenanceExpense(@Param("year") int year, @Param("month") int month);
    
    // Fetch maintenance events created by a specific user (for Users)
    List<MaintenanceEvent> findByCreatedByOrderByDateDesc(User createdBy);

    // Check if an event exists and belongs to the user (for update)
    @Query("SELECT m FROM MaintenanceEvent m WHERE m.id = :eventId AND m.createdBy.userId = :userId")
    MaintenanceEvent findByIdAndCreatedBy(@Param("eventId") Long eventId, @Param("userId") String userId);
    
    // Fetch maintenance events created by a specific user
    @Query("SELECT m FROM MaintenanceEvent m WHERE m.createdBy.userId = :userId ORDER BY m.date DESC")
    List<MaintenanceEvent> findByUserIdOrderByDateDesc(@Param("userId") String userId);

    @Query(value = "SELECT DATE_FORMAT(m.date, '%Y-%m-%d') as maintenanceDate, SUM(m.maintenance_Cost) " +
            "FROM maintenance_events m " +
            "WHERE YEAR(m.date) = :year AND MONTH(m.date) = :month " +
            "GROUP BY maintenanceDate " +
            "ORDER BY maintenanceDate ASC", nativeQuery = true)
    List<Object[]> getMaintenanceExpenseGroupedByDate(@Param("year") int year, @Param("month") int month);

    List<MaintenanceEvent> findByVehicleAndDateBetween(Vehicle vehicle, LocalDate start, LocalDate end);

    List<MaintenanceEvent> findByCreatedBy(User user);

}

