package com.erms.service;

import com.erms.entity.User;
import com.erms.entity.Volunteer;
import com.erms.enums.UserRole;
import com.erms.repository.UserRepository;
import com.erms.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final VolunteerRepository volunteerRepo;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(String username,
                             String email,
                             String password,
                             String fullName,
                             String phone,
                             UserRole role) {
        return registerUser(username, email, password, fullName, phone, role, null, null, null, null);
    }

    public User registerUser(String username,
                             String email,
                             String password,
                             String fullName,
                             String phone,
                             UserRole role,
                             String teamName,
                             String specialization,
                             Double currentLatitude,
                             Double currentLongitude) {

        if (userRepo.existsByUsername(username)) {
            throw new RuntimeException("Username already taken");
        }

        if (userRepo.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .phoneNumber(phone)
                .role(role)
                .build();

        user = userRepo.save(user);

        if (role == UserRole.VOLUNTEER) {
            Volunteer volunteer = Volunteer.builder()
                    .user(user)
                    .teamName(teamName)
                    .specialization(specialization)
                    .currentLatitude(currentLatitude)
                    .currentLongitude(currentLongitude)
                    .build();
            volunteerRepo.save(volunteer);
        }

        return user;
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void toggleUserStatus(Long id) {
        userRepo.findById(id).ifPresent(user -> {
            user.setActive(!user.isActive());
            userRepo.save(user);
        });
    }
}
