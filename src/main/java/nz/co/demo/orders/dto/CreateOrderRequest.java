package nz.co.demo.orders.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank @Size(max = 100) String customerName,
        @NotBlank @Size(max = 150) String product,
        @NotNull @Positive Integer quantity,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal unitPrice
) {}
