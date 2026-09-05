package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.OrderBatchRequest;
import com.freightfox.dispatch.model.DeliveryOrder;
import com.freightfox.dispatch.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void saveOrders(OrderBatchRequest request) {
        if (request == null || request.getOrders() == null) {
            return;
        }

        List<DeliveryOrder> orders = request.getOrders().stream()
                .map(req -> new DeliveryOrder(
                        req.getOrderId().trim(),
                        req.getLatitude(),
                        req.getLongitude(),
                        req.getAddress().trim(),
                        req.getPackageWeight(),
                        req.getPriority()
                ))
                .toList();

        orderRepository.saveAll(orders);
        log.info("Saved {} orders. Total orders in store: {}", orders.size(), orderRepository.count());
    }

    public Collection<DeliveryOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<DeliveryOrder> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public boolean existsOrder(String orderId) {
        return orderRepository.existsById(orderId);
    }

    public boolean deleteOrder(String orderId) {
        return orderRepository.deleteById(orderId);
    }

    public void clearAll() {
        orderRepository.deleteAll();
    }
}
