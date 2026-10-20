package com.bojan.inventoryreservation.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long productId) {
        super("Product with id " + productId + " does not exist");
    }
}
