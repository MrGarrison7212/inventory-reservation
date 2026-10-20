package com.bojan.inventoryreservation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.bojan.inventoryreservation.exception.InsufficientStockException;
import com.bojan.inventoryreservation.persistence.model.Product;
import com.bojan.inventoryreservation.persistence.repository.ProductRepository;
import com.bojan.inventoryreservation.persistence.repository.ReservationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Proves the pessimistic row lock prevents overselling. Deliberately not @Transactional:
 * every thread must run its own real transaction against H2.
 */
@SpringBootTest
class ReservationConcurrencyTest {

    // 2x the Hikari pool size (10): real contention without starving the pool.
    private static final int THREADS = 20;
    private static final int STOCK = 10;
    private static final long TIMEOUT_SECONDS = 30;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void concurrentReservationsNeverExceedAvailableStock() throws Exception {
        Product product = productRepository.save(new Product("Contended product", STOCK));
        long reservationsBefore = reservationRepository.count();

        // Fixed pool of exactly THREADS so every task can reach the start latch.
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch ready = new CountDownLatch(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();

        try {
            for (int i = 0; i < THREADS; i++) {
                results.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    try {
                        reservationService.reserve(product.getId(), 1);
                        return true;
                    } catch (InsufficientStockException e) {
                        return false;
                    }
                }));
            }

            assertThat(ready.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            int successes = 0;
            int rejections = 0;
            for (Future<Boolean> result : results) {
                // Any unexpected exception surfaces here as ExecutionException and fails the test.
                if (result.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    successes++;
                } else {
                    rejections++;
                }
            }

            assertThat(successes).isEqualTo(STOCK);
            assertThat(rejections).isEqualTo(THREADS - STOCK);
            assertThat(reservationService.getAvailableStock(product.getId())).isZero();
            assertThat(reservationRepository.count() - reservationsBefore).isEqualTo(STOCK);
        } finally {
            executor.shutdownNow();
        }
    }
}
