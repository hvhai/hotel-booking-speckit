package com.codehunter.hotelbooking.repository;

import com.codehunter.hotelbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
}

