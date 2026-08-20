package com.techfix.api.config;

import com.techfix.api.entities.User;
import com.techfix.api.enums.UserRole;
import com.techfix.api.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Only seed if no users exist yet
        if (userRepository.count() > 0) {
            log.info("Users already seeded, skipping...");
            return;
        }

        log.info("Seeding demo user accounts...");

        // Demo Customer
        User customer = new User(
                "Kamal Perera",
                "customer@techfix.lk",
                "+94 77 123 4567",
                passwordEncoder.encode("password123"),
                UserRole.CUSTOMER
        );
        userRepository.save(customer);

        // Staff member at Colombo branch
        User staffColombo = new User(
                "Nimal Silva",
                "staff.colombo@techfix.lk",
                "+94 71 234 5678",
                passwordEncoder.encode("staff123"),
                UserRole.STAFF
        );
        staffColombo.setBranchId(1L);
        userRepository.save(staffColombo);

        // Staff member at Galle branch
        User staffGalle = new User(
                "Sunil Jayawardena",
                "staff.galle@techfix.lk",
                "+94 76 345 6789",
                passwordEncoder.encode("staff123"),
                UserRole.STAFF
        );
        staffGalle.setBranchId(2L);
        userRepository.save(staffGalle);

        // Admin user
        User admin = new User(
                "TechFix Admin",
                "admin@techfix.lk",
                "+94 11 234 5678",
                passwordEncoder.encode("admin123"),
                UserRole.ADMIN
        );
        userRepository.save(admin);

        log.info("Seeded {} demo user accounts", userRepository.count());
    }
}
