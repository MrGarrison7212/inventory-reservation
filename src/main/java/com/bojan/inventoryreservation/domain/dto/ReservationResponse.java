package com.bojan.inventoryreservation.domain.dto;

import com.bojan.inventoryreservation.persistence.model.Reservation;
import com.bojan.inventoryreservation.persistence.model.ReservationStatus;
import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        Long productId,
        int quantity,
        ReservationStatus status,
        Instant createdAt) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getProductId(),
                reservation.getQuantity(),
                reservation.getStatus(),
                reservation.getCreatedAt());
    }
}
