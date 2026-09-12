package nz.co.demo.orders.controller;

import nz.co.demo.orders.dto.OrderResponse;
import nz.co.demo.orders.entity.OrderStatus;
import nz.co.demo.orders.exception.GlobalExceptionHandler;
import nz.co.demo.orders.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.Instant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {
    @Autowired MockMvc mvc;
    @MockBean OrderService service;

    @Test
    void returns201ForValidOrder() throws Exception {
        var response = new OrderResponse(1L, "Alex", "Keyboard", 2, new BigDecimal("49.90"),
                new BigDecimal("99.80"), OrderStatus.CREATED, Instant.parse("2026-01-01T00:00:00Z"));
        when(service.create(any())).thenReturn(response);

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerName":"Alex","product":"Keyboard","quantity":2,"unitPrice":49.90}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalPrice").value(99.80));
    }

    @Test
    void returns400ForInvalidOrder() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerName":"","product":"Keyboard","quantity":0,"unitPrice":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.customerName").exists())
                .andExpect(jsonPath("$.validationErrors.quantity").exists());
    }
}
