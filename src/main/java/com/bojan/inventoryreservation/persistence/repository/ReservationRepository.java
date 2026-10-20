package com.bojan.inventoryreservation.persistence.repository;

import com.bojan.inventoryreservation.persistence.model.Reservation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
}
