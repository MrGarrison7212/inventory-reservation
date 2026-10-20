package com.bojan.inventoryreservation.config;

import com.bojan.inventoryreservation.persistence.model.Product;
import com.bojan.inventoryreservation.persistence.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Loads test products at startup; a product management API is out of scope. */
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.saveAll(List.of(
                new Product("Mechanical Keyboard", 10),
                new Product("USB-C Cable", 3),
                new Product("Limited Edition Mug", 0)));
    }
}
