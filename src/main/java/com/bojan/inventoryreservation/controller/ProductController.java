package com.bojan.inventoryreservation.controller;

import com.bojan.inventoryreservation.domain.dto.StockResponse;
import com.bojan.inventoryreservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ReservationService reservationService;

    @GetMapping("/{productId}/stock")
    public StockResponse getStock(@PathVariable Long productId) {
        return new StockResponse(productId, reservationService.getAvailableStock(productId));
    }
}
