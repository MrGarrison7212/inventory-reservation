package com.bojan.inventoryreservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bojan.inventoryreservation.exception.InsufficientStockException;
import com.bojan.inventoryreservation.exception.ProductNotFoundException;
import com.bojan.inventoryreservation.persistence.model.Product;
import com.bojan.inventoryreservation.persistence.model.Reservation;
import com.bojan.inventoryreservation.persistence.model.ReservationStatus;
import com.bojan.inventoryreservation.persistence.repository.ProductRepository;
import com.bojan.inventoryreservation.persistence.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// Not @Transactional: the service must commit on its own, exactly as in production.
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void reserveCreatesReservationAndDecreasesStock() {
        Product product = productRepository.save(new Product("Test product", 10));

        Reservation reservation = reservationService.reserve(product.getId(), 3);

        assertThat(reservation.getId()).isNotNull();
        assertThat(reservation.getProductId()).isEqualTo(product.getId());
        assertThat(reservation.getQuantity()).isEqualTo(3);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(reservation.getCreatedAt()).isNotNull();
        assertThat(reservationService.getAvailableStock(product.getId())).isEqualTo(7);
    }

    @Test
    void reserveExactlyRemainingStockSucceeds() {
        Product product = productRepository.save(new Product("Test product", 5));

        reservationService.reserve(product.getId(), 5);

        assertThat(reservationService.getAvailableStock(product.getId())).isZero();
    }

    @Test
    void reserveMoreThanAvailableIsRejectedAndLeavesStockUnchanged() {
        Product product = productRepository.save(new Product("Test product", 5));
        long reservationsBefore = reservationRepository.count();

        assertThatThrownBy(() -> reservationService.reserve(product.getId(), 6))
                .isInstanceOf(InsufficientStockException.class);

        assertThat(reservationService.getAvailableStock(product.getId())).isEqualTo(5);
        assertThat(reservationRepository.count()).isEqualTo(reservationsBefore);
    }

    @Test
    void reserveUnknownProductThrowsNotFound() {
        assertThatThrownBy(() -> reservationService.reserve(-1L, 1))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void getAvailableStockForUnknownProductThrowsNotFound() {
        assertThatThrownBy(() -> reservationService.getAvailableStock(-1L))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
