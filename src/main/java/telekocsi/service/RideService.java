package telekocsi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telekocsi.exception.RideNotFoundException;
import telekocsi.exception.UserNotFoundException;
import telekocsi.model.Ride;
import telekocsi.model.User;
import telekocsi.repository.RideRepository;
import telekocsi.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Collections;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public RideService(RideRepository rideRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    @Transactional
    public void removePassengerFromRide(long rideId, long userId) {
        Ride ride = findRideById(rideId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (!ride.getPassengers().contains(user)) {
            throw new IllegalArgumentException("You are not a passenger on this ride.");
        }

        ride.getPassengers().remove(user);
        ride.setSeatsAvailable(ride.getSeatsAvailable() + 1);

        rideRepository.save(ride);
    }

    public List<Ride> searchRides(String from, String to, String dateStr) {
        if (from != null && from.trim().isEmpty()) from = null;
        if (to != null && to.trim().isEmpty()) to = null;

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dateStr);
                startDate = date.atStartOfDay();
                endDate = date.plusDays(1).atStartOfDay();
            } catch (Exception e1) {
                try {
                    Year year = Year.parse(dateStr);
                    startDate = year.atDay(1).atStartOfDay();
                    endDate = year.plusYears(1).atDay(1).atStartOfDay();
                } catch (Exception e2) {
                    return Collections.emptyList();
                }
            }
        }

        return rideRepository.searchRides(from, to, startDate, endDate);
    }

    public Ride findRideById(long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with ID=" + id));
    }

    public List<Ride> findRidesByDriver(User driver) {
        return rideRepository.findByDriver(driver);
    }

    public List<Ride> findRidesByPassenger(User passenger) {
        return rideRepository.findByPassengersContaining(passenger);
    }

    public void saveRide(Ride ride) {
        if (ride.getId() == null) {
            ride.setCreatedDate(LocalDateTime.now());
        } else {
            ride.setLastModifiedDate(LocalDateTime.now());
        }

        if (ride.getStartTime() != null && ride.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("The start time cannot be in the past!");
        }

        rideRepository.save(ride);
    }

    @Transactional
    public void addPassengerToRide(long rideId, long userId) {
        Ride ride = findRideById(rideId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (ride.getDriver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("The driver cannot join their own ride as a passenger!");
        }

        if (ride.getPassengers().contains(user)) {
            throw new IllegalArgumentException("This passenger is already part of the ride.");
        }
        if (ride.getSeatsAvailable() <= 0) {
            throw new IllegalArgumentException("No seats available.");
        }

        ride.getPassengers().add(user);
        ride.setSeatsAvailable(ride.getSeatsAvailable() - 1);

        rideRepository.save(ride);
    }

    public void deleteRideById(long id) {
        if (!rideRepository.existsById(id)) {
            throw new RideNotFoundException("Ride not found with ID=" + id);
        }
        rideRepository.deleteById(id);
    }
}