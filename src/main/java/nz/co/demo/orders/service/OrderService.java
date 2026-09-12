package nz.co.demo.orders.service;

import nz.co.demo.orders.dto.CreateOrderRequest;
import nz.co.demo.orders.dto.OrderResponse;
import nz.co.demo.orders.entity.Order;
import nz.co.demo.orders.exception.OrderNotFoundException;
import nz.co.demo.orders.repository.OrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Order order = new Order(request.customerName(), request.product(), request.quantity(), request.unitPrice());
        return OrderResponse.from(repository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return repository.findById(id).map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(OrderResponse::from).toList();
    }
}
