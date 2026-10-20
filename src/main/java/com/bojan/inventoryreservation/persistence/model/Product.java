package com.bojan.inventoryreservation.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int availableQuantity;

    public Product(String name, int availableQuantity) {
        this.name = name;
        this.availableQuantity = availableQuantity;
    }

    public boolean hasAvailable(int quantity) {
        return availableQuantity >= quantity;
    }

    /** Callers must hold the row lock and check {@link #hasAvailable(int)} first. */
    public void decreaseAvailable(int quantity) {
        availableQuantity -= quantity;
    }
}
