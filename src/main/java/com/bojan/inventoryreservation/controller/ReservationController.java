package com.bojan.inventoryreservation.controller;

import com.bojan.inventoryreservation.domain.dto.ReservationRequest;
import com.bojan.inventoryreservation.domain.dto.ReservationResponse;
import com.bojan.inventoryreservation.persistence.model.Reservation;
import com.bojan.inventoryreservation.service.ReservationService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.reserve(request.productId(), request.quantity());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservation.getId())
                .toUri();
        return ResponseEntity.created(location).body(ReservationResponse.from(reservation));
    }
}
