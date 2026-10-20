package com.bojan.inventoryreservation.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bojan.inventoryreservation.exception.ProductNotFoundException;
import com.bojan.inventoryreservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void getStockReturnsAvailableQuantity() throws Exception {
        given(reservationService.getAvailableStock(1L)).willReturn(10);

        mockMvc.perform(get("/api/products/1/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.availableQuantity").value(10));
    }

    @Test
    void getStockForUnknownProductReturns404() throws Exception {
        given(reservationService.getAvailableStock(99L)).willThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/products/99/stock"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Product with id 99 does not exist"));
    }

    @Test
    void getStockWithNonNumericIdReturns400() throws Exception {
        mockMvc.perform(get("/api/products/abc/stock"))
                .andExpect(status().isBadRequest());
    }
}
