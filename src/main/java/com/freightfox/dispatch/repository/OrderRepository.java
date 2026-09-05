package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.model.DeliveryOrder;
import java.util.Collection;
import java.util.Optional;

public interface OrderRepository {

    DeliveryOrder save(DeliveryOrder order);

    void saveAll(Collection<DeliveryOrder> orders);

    Optional<DeliveryOrder> findById(String orderId);

    boolean existsById(String orderId);

    Collection<DeliveryOrder> findAll();

    int count();

    boolean deleteById(String orderId);

    void deleteAll();
}
