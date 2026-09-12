package nz.co.demo.orders.controller;

import jakarta.validation.Valid;
import nz.co.demo.orders.dto.CreateOrderRequest;
import nz.co.demo.orders.dto.OrderResponse;
import nz.co.demo.orders.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<OrderResponse> getAll() {
        return service.getAll();
    }
}
