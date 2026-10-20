package com.bojan.inventoryreservation.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bojan.inventoryreservation.exception.InsufficientStockException;
import com.bojan.inventoryreservation.exception.ProductNotFoundException;
import com.bojan.inventoryreservation.persistence.model.Reservation;
import com.bojan.inventoryreservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    private static final String URL = "/api/reservations";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void createReturns201WithBodyAndLocation() throws Exception {
        Reservation reservation = new Reservation(1L, 3);
        ReflectionTestUtils.setField(reservation, "id", java.util.UUID.fromString("11111111-1111-1111-1111-111111111111"));
        given(reservationService.reserve(1L, 3)).willReturn(reservation);

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":3}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/reservations/11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.status").value("RESERVED"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"productId\":1,\"quantity\":0}",
            "{\"productId\":1,\"quantity\":-5}",
            "{\"productId\":1}",
            "{\"quantity\":3}",
            "{}"
    })
    void createWithInvalidFieldsReturns400(String body) throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "not json",
            "{\"productId\":\"abc\",\"quantity\":3}",
            "{\"productId\":1,\"quantity\":99999999999}"
    })
    void createWithMalformedBodyReturns400(String body) throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithMissingBodyReturns400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createForUnknownProductReturns404() throws Exception {
        given(reservationService.reserve(anyLong(), anyInt())).willThrow(new ProductNotFoundException(99L));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":99,\"quantity\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Product with id 99 does not exist"));
    }

    @Test
    void createWithInsufficientStockReturns409() throws Exception {
        given(reservationService.reserve(anyLong(), anyInt()))
                .willThrow(new InsufficientStockException(1L, 5, 2));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":5}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Insufficient stock for product 1: requested 5, available 2"));
    }
}
