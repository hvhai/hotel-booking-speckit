package com.codehunter.hotelbooking.repository;

import com.codehunter.hotelbooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    boolean existsByRoomNumber(String roomNumber);

    Optional<Room> findByRoomNumber(String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.id NOT IN " +
           "(SELECT DISTINCT b.room.id FROM Booking b WHERE b.status = 'ACTIVE' AND " +
           "(b.checkIn <= :endDate AND b.checkOut > :startDate))")
    List<Room> findAvailableRoomsInDateRange(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}

