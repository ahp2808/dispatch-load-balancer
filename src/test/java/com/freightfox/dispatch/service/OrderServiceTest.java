package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.OrderBatchRequest;
import com.freightfox.dispatch.dto.OrderRequest;
import com.freightfox.dispatch.model.DeliveryOrder;
import com.freightfox.dispatch.model.Priority;
import com.freightfox.dispatch.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository);
    }

    @Test
    void testSaveOrdersBatch() {
        OrderRequest req = new OrderRequest("O1", 12.97, 77.59, "MG Road", 10.0, Priority.HIGH);
        OrderBatchRequest batch = new OrderBatchRequest(List.of(req));

        orderService.saveOrders(batch);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<DeliveryOrder>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(orderRepository, times(1)).saveAll(captor.capture());

        Collection<DeliveryOrder> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        DeliveryOrder order = saved.iterator().next();
        assertThat(order.getOrderId()).isEqualTo("O1");
        assertThat(order.getPackageWeight()).isEqualTo(10.0);
    }

    @Test
    void testSaveOrdersNullRequest() {
        orderService.saveOrders(null);
        verify(orderRepository, never()).saveAll(any());

        orderService.saveOrders(new OrderBatchRequest(null));
        verify(orderRepository, never()).saveAll(any());
    }

    @Test
    void testGetAllOrders() {
        DeliveryOrder o1 = new DeliveryOrder("O1", 10, 20, "A1", 5, Priority.HIGH);
        when(orderRepository.findAll()).thenReturn(List.of(o1));

        Collection<DeliveryOrder> result = orderService.getAllOrders();
        assertThat(result).containsExactly(o1);
    }

    @Test
    void testGetOrderById() {
        DeliveryOrder o1 = new DeliveryOrder("O1", 10, 20, "A1", 5, Priority.HIGH);
        when(orderRepository.findById("O1")).thenReturn(Optional.of(o1));

        Optional<DeliveryOrder> found = orderService.getOrderById("O1");
        assertThat(found).isPresent().contains(o1);
    }

    @Test
    void testExistsOrder() {
        when(orderRepository.existsById("O1")).thenReturn(true);
        when(orderRepository.existsById("UNKNOWN")).thenReturn(false);

        assertThat(orderService.existsOrder("O1")).isTrue();
        assertThat(orderService.existsOrder("UNKNOWN")).isFalse();
    }

    @Test
    void testDeleteOrder() {
        when(orderRepository.deleteById("O1")).thenReturn(true);
        boolean deleted = orderService.deleteOrder("O1");
        assertThat(deleted).isTrue();
        verify(orderRepository, times(1)).deleteById("O1");
    }

    @Test
    void testClearAll() {
        orderService.clearAll();
        verify(orderRepository, times(1)).deleteAll();
    }
}
