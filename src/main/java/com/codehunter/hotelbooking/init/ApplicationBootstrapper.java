package com.codehunter.hotelbooking.init;

import com.codehunter.hotelbooking.model.Room;
import com.codehunter.hotelbooking.model.User;
import com.codehunter.hotelbooking.model.User.MembershipLevel;
import com.codehunter.hotelbooking.repository.RoomRepository;
import com.codehunter.hotelbooking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
public class ApplicationBootstrapper implements ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;
    private final RoomRepository roomRepository;

    public ApplicationBootstrapper(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   @Value("${app.default-user-password}") String defaultPassword,
                                   RoomRepository roomRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultPassword = defaultPassword;
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Application started");
        createIfNotExists("user1", "user1@example.com");
        createIfNotExists("user2", "user2@example.com");
        createAdminIfNotExists("admin", "admin@example.com");

        createRoomIfNotExists("101", "Single", new java.math.BigDecimal("100.00"));
        createRoomIfNotExists("102", "Double", new java.math.BigDecimal("150.00"));
        createRoomIfNotExists("103", "Suite", new java.math.BigDecimal("250.00"));
        createRoomIfNotExists("104", "Deluxe", new java.math.BigDecimal("200.00"));
        createRoomIfNotExists("105", "Family", new java.math.BigDecimal("180.00"));
    }

    private void createIfNotExists(String username, String email) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setMembershipLevel(MembershipLevel.CLASSIC);
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        }
    }

    private void createAdminIfNotExists(String username, String email) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setMembershipLevel(MembershipLevel.DIAMOND);
            user.setRole(User.Role.ADMIN);
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
            log.info("Created admin user: {}", username);
        }
    }

    private void createRoomIfNotExists(String roomNumber, String type, java.math.BigDecimal pricePerNight) {
        if (!roomRepository.existsByRoomNumber(roomNumber)) {
            com.codehunter.hotelbooking.model.Room room = new com.codehunter.hotelbooking.model.Room();
            room.setRoomNumber(roomNumber);
            room.setType(type);
            room.setPricePerNight(pricePerNight);
            Room save = roomRepository.save(room);
            log.info("Created room: {} ", save);
        }
    }

}
