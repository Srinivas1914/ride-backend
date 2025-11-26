package com.blablacar.repository;
import com.blablacar.entity.Ride;
import com.blablacar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriver(User driver);
    List<Ride> findByStatus(Ride.RideStatus status);

    @Query("SELECT r FROM Ride r WHERE r.departureCity LIKE %:departure% " +
           "AND r.destinationCity LIKE %:destination% " +
           "AND r.departureDate >= :date " +
           "AND r.status = 'ACTIVE' " +
           "AND r.availableSeats >= :seats")
    List<Ride> searchRides(@Param("departure") String departure,
                          @Param("destination") String destination,
                          @Param("date") LocalDate date,
                          @Param("seats") Integer seats);

    List<Ride> findTop10ByStatusOrderByCreatedAtDesc(Ride.RideStatus status);
}
