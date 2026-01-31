package telekocsi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import telekocsi.exception.UserNotFoundException;
import telekocsi.model.Ride;
import telekocsi.model.Role;
import telekocsi.model.User;
import telekocsi.repository.RideRepository;
import telekocsi.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RideRepository rideRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getPotentialDriversForAdmin(String adminUsername) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(adminUsername))
                .collect(Collectors.toList());
    }

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID=" + id));
    }

    public Optional<User> findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }


    public void saveUserByAdmin(User user) {
        if (user.getId() == null) {
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username is already taken!");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getRole() == null) user.setRole(Role.USER);
            userRepository.save(user);

        } else {
            User existingUser = findUserById(user.getId());

            if ("admin".equals(existingUser.getUsername()) && !user.getUsername().equals("admin")) {
                throw new IllegalArgumentException("You cannot change the username of the main 'admin' account!");
            }

            existingUser.setName(user.getName());
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setAge(user.getAge());

            if ("admin".equals(existingUser.getUsername())) {
                existingUser.setRole(Role.ADMIN);
            } else if (user.getRole() != null) {
                existingUser.setRole(user.getRole());
            }

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            userRepository.save(existingUser);
        }
    }



    public void deleteUserById(long id, String currentAdminUsername) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        if ("admin".equals(userToDelete.getUsername())) {
            throw new IllegalArgumentException("CRITICAL: The main 'admin' account cannot be deleted!");
        }

        if (userToDelete.getUsername().equals(currentAdminUsername)) {
            throw new IllegalArgumentException("You cannot delete admin!");
        }

        if (rideRepository.existsByDriverId(id)) {
            throw new IllegalArgumentException("Cannot delete: User has active rides as a driver!");
        }

        List<Ride> ridesAsPassenger = rideRepository.findByPassengersContaining(userToDelete);
        for (Ride ride : ridesAsPassenger) {
            ride.getPassengers().remove(userToDelete);
            ride.setSeatsAvailable(ride.getSeatsAvailable() + 1);
            rideRepository.save(ride);
        }

        userRepository.deleteById(id);
    }
}