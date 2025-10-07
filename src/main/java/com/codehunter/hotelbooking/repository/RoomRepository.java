package com.codehunter.hotelbooking.repository;

import com.codehunter.hotelbooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    boolean existsByRoomNumber(String roomNumber);
}

