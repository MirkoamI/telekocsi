package telekocsi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import telekocsi.model.Ride;
import telekocsi.model.User;
import telekocsi.repository.RideRepository;
import telekocsi.repository.UserRepository;
import telekocsi.model.Role;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RideRepository rideRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("--- DATABASE INITIALIZATION ---");

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("email");
            admin.setName("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("-> Admin created (user: admin / pass: admin123)");

            User u1 = new User();
            u1.setUsername("sofor1");
            u1.setPassword(passwordEncoder.encode("jelszo123"));
            u1.setName("Kovács János");
            u1.setEmail("janos@test.com");
            u1.setRole(Role.USER);
            u1.setAge(30);
            u1.setPhone("06-30-111-2222");
            userRepository.save(u1);
            System.out.println("-> Driver created (user: sofor1 / pass: jelszo123)");

            User u2 = new User();
            u2.setUsername("utas1");
            u2.setPassword(passwordEncoder.encode("jelszo123"));
            u2.setName("Nagy Anna");
            u2.setEmail("anna@test.com");
            u2.setRole(Role.USER);
            u2.setAge(22);
            u2.setPhone("06-20-333-4444");
            userRepository.save(u2);
            System.out.println("-> Passenger created (user: utas1 / pass: jelszo123)");

            Ride r1 = new Ride();
            r1.setStartPlace("Budapest");
            r1.setEndPlace("Szeged");
            r1.setStartTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
            r1.setSeatsAvailable(3);
            r1.setDriver(u1);
            rideRepository.save(r1);

            Ride r2 = new Ride();
            r2.setStartPlace("Pécs");
            r2.setEndPlace("Debrecen");
            r2.setStartTime(LocalDateTime.now().plusDays(5).withHour(8).withMinute(30));
            r2.setSeatsAvailable(2);
            r2.setDriver(u2);
            rideRepository.save(r2);

            System.out.println("--- INITIALIZATION COMPLETE ---");
        }
    }
}