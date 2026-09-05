package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.model.DeliveryOrder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, DeliveryOrder> orderMap = new ConcurrentHashMap<>();

    @Override
    public DeliveryOrder save(DeliveryOrder order) {
        if (order == null || order.getOrderId() == null) {
            throw new IllegalArgumentException("Order and orderId cannot be null");
        }
        orderMap.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public void saveAll(Collection<DeliveryOrder> orders) {
        if (orders != null) {
            for (DeliveryOrder order : orders) {
                save(order);
            }
        }
    }

    @Override
    public Optional<DeliveryOrder> findById(String orderId) {
        if (orderId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(orderMap.get(orderId));
    }

    @Override
    public boolean existsById(String orderId) {
        return orderId != null && orderMap.containsKey(orderId);
    }

    @Override
    public Collection<DeliveryOrder> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    @Override
    public int count() {
        return orderMap.size();
    }

    @Override
    public boolean deleteById(String orderId) {
        if (orderId == null) {
            return false;
        }
        return orderMap.remove(orderId) != null;
    }

    @Override
    public void deleteAll() {
        orderMap.clear();
    }
}
