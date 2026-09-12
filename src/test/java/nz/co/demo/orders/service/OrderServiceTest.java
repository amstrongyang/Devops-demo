package nz.co.demo.orders.service;

import nz.co.demo.orders.dto.CreateOrderRequest;
import nz.co.demo.orders.entity.Order;
import nz.co.demo.orders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private final OrderRepository repository = mock(OrderRepository.class);
    private final OrderService service = new OrderService(repository);

    @Test
    void createsAnOrderAndCalculatesTotal() {
        when(repository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(new CreateOrderRequest("Alex", "Keyboard", 2, new BigDecimal("49.90")));

        assertThat(response.totalPrice()).isEqualByComparingTo("99.80");
        assertThat(response.status().name()).isEqualTo("CREATED");
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getProduct()).isEqualTo("Keyboard");
    }
}
