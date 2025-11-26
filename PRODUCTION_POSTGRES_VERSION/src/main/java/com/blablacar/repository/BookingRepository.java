package com.blablacar.repository;
import com.blablacar.entity.Booking;
import com.blablacar.entity.User;
import com.blablacar.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPassenger(User passenger);
    List<Booking> findByRide(Ride ride);
    List<Booking> findByRideDriver(User driver);
    boolean existsByPassengerAndRide(User passenger, Ride ride);
}
