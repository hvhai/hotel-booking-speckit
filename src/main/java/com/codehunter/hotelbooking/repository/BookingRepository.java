package com.codehunter.hotelbooking.repository;

import com.codehunter.hotelbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.status = 'ACTIVE' AND " +
           "((b.checkIn <= :endDate AND b.checkOut > :startDate))")
    List<Booking> findActiveBookingsByRoomIdAndDateRange(
            @Param("roomId") UUID roomId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}

