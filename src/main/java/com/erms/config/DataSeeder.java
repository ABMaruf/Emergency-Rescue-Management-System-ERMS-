package com.erms.config;

import com.erms.entity.User;
import com.erms.entity.Volunteer;
import com.erms.enums.AvailabilityStatus;
import com.erms.enums.UserRole;
import com.erms.repository.UserRepository;
import com.erms.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdmin();
        seedVolunteer();
        seedUser();
    }

    private void seedAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .fullName("System Admin")
                    .username("admin")
                    .email("admin@erms.com")
                    .phoneNumber("01700000000")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .role(UserRole.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);
        }
    }

    private void seedVolunteer() {
        if (userRepository.findByUsername("volunteer1").isEmpty()) {
            User volunteerUser = User.builder()
                    .fullName("Rakib Volunteer")
                    .username("volunteer1")
                    .email("volunteer1@erms.com")
                    .phoneNumber("01711111111")
                    .passwordHash(passwordEncoder.encode("Volunteer@123"))
                    .role(UserRole.VOLUNTEER)
                    .active(true)
                    .build();

            User savedVolunteerUser = userRepository.save(volunteerUser);

            Volunteer volunteer = Volunteer.builder()
                    .user(savedVolunteerUser)
                    .teamName("Rescue Team Alpha")
                    .specialization("Medical Support")
                    .availabilityStatus(AvailabilityStatus.AVAILABLE)
                    .completedTasks(0)
                    .rating(BigDecimal.valueOf(5.00))
                    .build();

            volunteerRepository.save(volunteer);
        }
    }

    private void seedUser() {
        if (userRepository.findByUsername("user1").isEmpty()) {
            User user = User.builder()
                    .fullName("Rahim User")
                    .username("user1")
                    .email("user1@erms.com")
                    .phoneNumber("01722222222")
                    .passwordHash(passwordEncoder.encode("User@123"))
                    .role(UserRole.USER)
                    .active(true)
                    .build();

            userRepository.save(user);
        }
    }
}
