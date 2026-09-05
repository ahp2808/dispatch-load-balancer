package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.model.DeliveryOrder;
import com.freightfox.dispatch.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
    }

    @Test
    void testSaveAndFindById() {
        DeliveryOrder order = new DeliveryOrder("ORD1", 12.97, 77.59, "MG Road", 10.0, Priority.HIGH);
        repository.save(order);

        Optional<DeliveryOrder> found = repository.findById("ORD1");
        assertThat(found).isPresent();
        assertThat(found.get().getAddress()).isEqualTo("MG Road");
    }

    @Test
    void testUpsertBehavior() {
        DeliveryOrder order1 = new DeliveryOrder("ORD1", 12.97, 77.59, "Old Address", 10.0, Priority.HIGH);
        repository.save(order1);

        DeliveryOrder updated = new DeliveryOrder("ORD1", 12.97, 77.59, "New Address", 15.0, Priority.HIGH);
        repository.save(updated);

        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findById("ORD1").get().getAddress()).isEqualTo("New Address");
        assertThat(repository.findById("ORD1").get().getPackageWeight()).isEqualTo(15.0);
    }

    @Test
    void testSaveNullOrderThrowsException() {
        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> repository.save(new DeliveryOrder(null, 0, 0, "Test", 1, Priority.LOW)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testSaveAllAndFindAll() {
        List<DeliveryOrder> orders = List.of(
                new DeliveryOrder("O1", 10, 20, "A1", 5, Priority.HIGH),
                new DeliveryOrder("O2", 11, 21, "A2", 8, Priority.MEDIUM)
        );
        repository.saveAll(orders);

        assertThat(repository.count()).isEqualTo(2);
        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void testDeleteById() {
        DeliveryOrder order = new DeliveryOrder("ORD1", 12.97, 77.59, "MG Road", 10.0, Priority.HIGH);
        repository.save(order);

        boolean deleted = repository.deleteById("ORD1");
        assertThat(deleted).isTrue();
        assertThat(repository.findById("ORD1")).isEmpty();

        boolean deletedAgain = repository.deleteById("ORD1");
        assertThat(deletedAgain).isFalse();
    }

    @Test
    void testNullKeyHandling() {
        assertThat(repository.findById(null)).isEmpty();
        assertThat(repository.deleteById(null)).isFalse();
    }

    @Test
    void testExistsById() {
        DeliveryOrder order = new DeliveryOrder("ORD1", 12.97, 77.59, "MG Road", 10.0, Priority.HIGH);
        repository.save(order);

        assertThat(repository.existsById("ORD1")).isTrue();
        assertThat(repository.existsById("NON_EXISTENT")).isFalse();
        assertThat(repository.existsById(null)).isFalse();
    }

    @Test
    void testSaveAllWithDuplicatesInBatch() {
        List<DeliveryOrder> ordersWithDuplicates = List.of(
                new DeliveryOrder("O1", 10, 20, "Old Address", 5, Priority.LOW),
                new DeliveryOrder("O2", 11, 21, "A2", 8, Priority.MEDIUM),
                new DeliveryOrder("O1", 10, 20, "Updated In Same Batch", 12, Priority.HIGH)
        );
        repository.saveAll(ordersWithDuplicates);

        assertThat(repository.count()).isEqualTo(2);
        DeliveryOrder o1 = repository.findById("O1").orElseThrow();
        assertThat(o1.getAddress()).isEqualTo("Updated In Same Batch");
        assertThat(o1.getPackageWeight()).isEqualTo(12.0);
        assertThat(o1.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void testSaveAllNullOrEmpty() {
        repository.saveAll(null);
        repository.saveAll(List.of());
        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    void testSaveAllWithInvalidElementThrowsException() {
        List<DeliveryOrder> orders = new java.util.ArrayList<>();
        orders.add(new DeliveryOrder("O1", 10, 20, "A1", 5, Priority.HIGH));
        orders.add(null);

        assertThatThrownBy(() -> repository.saveAll(orders))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testConcurrentBatchUpserts() {
        java.util.stream.IntStream.range(0, 100).parallel().forEach(i -> {
            String id = "ORD-" + (i % 10);
            repository.save(new DeliveryOrder(id, 12.0 + i, 77.0 + i, "Addr " + i, 5.0 + i, Priority.HIGH));
        });

        assertThat(repository.count()).isEqualTo(10);
        for (int i = 0; i < 10; i++) {
            assertThat(repository.existsById("ORD-" + i)).isTrue();
        }
    }

    @Test
    void testDeleteAll() {
        repository.save(new DeliveryOrder("O1", 10, 20, "A1", 5, Priority.HIGH));
        repository.deleteAll();
        assertThat(repository.count()).isEqualTo(0);
        assertThat(repository.findAll()).isEmpty();
    }
}
