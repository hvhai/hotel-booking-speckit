package com.codehunter.hotelbooking.repository;

import com.codehunter.hotelbooking.model.Cancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CancellationRepository extends JpaRepository<Cancellation, UUID> {
}
