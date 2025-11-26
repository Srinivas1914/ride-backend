package com.blablacar.service;

import com.blablacar.entity.Ride;
import com.blablacar.entity.User;
import com.blablacar.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeSampleRides() {
        if (rideRepository.count() == 0) {
            User driver = userRepository.findByEmail("driver@demo.com").orElse(null);
            if (driver != null) {
                // Create sample rides
                for (int i = 1; i <= 5; i++) {
                    Ride ride = new Ride();
                    ride.setDriver(driver);
                    ride.setDepartureCity("Hyderabad");
                    ride.setDestinationCity(i % 2 == 0 ? "Bangalore" : "Pune");
                    ride.setDepartureDate(LocalDate.now().plusDays(i));
                    ride.setDepartureTime(LocalTime.of(9 + i, 0));
                    ride.setTotalSeats(4);
                    ride.setAvailableSeats(4 - (i % 3));
                    ride.setPricePerSeat(500.0 + (i * 50));
                    ride.setSmokingAllowed(false);
                    ride.setInstantBooking(true);
                    ride.setNotes("Demo ride - " + i);
                    ride.setStatus(Ride.RideStatus.ACTIVE);
                    ride.setViews(10 + (i * 5));
                    ride.setBookings(i % 3);
                    rideRepository.save(ride);
                }
                System.out.println("✅ 5 DEMO RIDES CREATED\n");
            }
        }
    }

    @Transactional
    public Ride createRide(Ride ride) {
        ride.setAvailableSeats(ride.getTotalSeats());
        ride.setStatus(Ride.RideStatus.ACTIVE);
        ride.setViews(0);
        ride.setBookings(0);
        ride.setCreatedAt(LocalDateTime.now());
        return rideRepository.save(ride);
    }

    public List<Ride> searchRides(String departure, String destination, LocalDate date, Integer seats) {
        if (date == null) date = LocalDate.now();
        if (seats == null) seats = 1;
        return rideRepository.searchRides(departure, destination, date, seats);
    }

    public List<Ride> getRecentRides() {
        return rideRepository.findTop10ByStatusOrderByCreatedAtDesc(Ride.RideStatus.ACTIVE);
    }

    public List<Ride> getDriverRides(User driver) {
        return rideRepository.findByDriver(driver);
    }

    public Ride getRideById(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
    }

    @Transactional
    public void incrementViews(Long rideId) {
        Ride ride = getRideById(rideId);
        ride.setViews(ride.getViews() + 1);
        rideRepository.save(ride);
    }

    @Transactional
    public void cancelRide(Long rideId) {
        Ride ride = getRideById(rideId);
        ride.setStatus(Ride.RideStatus.CANCELLED);
        rideRepository.save(ride);
    }
}
