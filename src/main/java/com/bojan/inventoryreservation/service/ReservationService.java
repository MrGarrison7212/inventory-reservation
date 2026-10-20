package com.bojan.inventoryreservation.service;

import com.bojan.inventoryreservation.exception.InsufficientStockException;
import com.bojan.inventoryreservation.exception.ProductNotFoundException;
import com.bojan.inventoryreservation.persistence.model.Product;
import com.bojan.inventoryreservation.persistence.model.Reservation;
import com.bojan.inventoryreservation.persistence.repository.ProductRepository;
import com.bojan.inventoryreservation.persistence.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public int getAvailableStock(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId))
                .getAvailableQuantity();
    }

    /**
     * Lock, check, decrement and insert happen in one transaction: the row lock serializes
     * concurrent reservations of the same product, and any failure rolls the stock change back.
     */
    @Transactional
    public Reservation reserve(Long productId, int quantity) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.hasAvailable(quantity)) {
            throw new InsufficientStockException(productId, quantity, product.getAvailableQuantity());
        }

        product.decreaseAvailable(quantity);
        return reservationRepository.save(new Reservation(productId, quantity));
    }
}
