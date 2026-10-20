package com.bojan.inventoryreservation.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue
    private UUID id;

    private Long productId;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Instant createdAt;

    public Reservation(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.status = ReservationStatus.RESERVED;
        this.createdAt = Instant.now();
    }
}
