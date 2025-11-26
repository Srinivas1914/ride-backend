package com.blablacar.service;

import com.blablacar.entity.Booking;
import com.blablacar.entity.Ride;
import com.blablacar.entity.User;
import com.blablacar.repository.BookingRepository;
import com.blablacar.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;

    @Transactional
    public Booking createBooking(Long rideId, User passenger, Integer seats, String message) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getDriver().getId().equals(passenger.getId())) {
            throw new RuntimeException("Cannot book your own ride");
        }

        if (bookingRepository.existsByPassengerAndRide(passenger, ride)) {
            throw new RuntimeException("Already booked this ride");
        }

        if (ride.getAvailableSeats() < seats) {
            throw new RuntimeException("Not enough seats available");
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPassenger(passenger);
        booking.setSeatsBooked(seats);
        booking.setTotalPrice(ride.getPricePerSeat() * seats);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setBookedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        ride.setAvailableSeats(ride.getAvailableSeats() - seats);
        ride.setBookings(ride.getBookings() + 1);
        if (ride.getAvailableSeats() == 0) {
            ride.setStatus(Ride.RideStatus.FULL);
        }
        rideRepository.save(ride);

        return savedBooking;
    }

    public List<Booking> getPassengerBookings(User passenger) {
        return bookingRepository.findByPassenger(passenger);
    }

    public List<Booking> getDriverBookings(User driver) {
        return bookingRepository.findByRideDriver(driver);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Ride ride = booking.getRide();
        ride.setAvailableSeats(ride.getAvailableSeats() + booking.getSeatsBooked());
        if (ride.getStatus() == Ride.RideStatus.FULL) {
            ride.setStatus(Ride.RideStatus.ACTIVE);
        }
        rideRepository.save(ride);
    }
}
