package nz.co.demo.orders.dto;

import nz.co.demo.orders.entity.Order;
import nz.co.demo.orders.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(Long id, String customerName, String product, Integer quantity,
                            BigDecimal unitPrice, BigDecimal totalPrice, OrderStatus status,
                            Instant createdAt) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getCustomerName(), order.getProduct(),
                order.getQuantity(), order.getUnitPrice(),
                order.getUnitPrice().multiply(BigDecimal.valueOf(order.getQuantity())),
                order.getStatus(), order.getCreatedAt());
    }
}
