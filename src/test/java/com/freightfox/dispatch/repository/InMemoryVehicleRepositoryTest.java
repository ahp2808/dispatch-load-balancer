package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryVehicleRepositoryTest {

    private InMemoryVehicleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVehicleRepository();
    }

    @Test
    void testSaveAndFindById() {
        Vehicle vehicle = new Vehicle("V1", 100, 12.97, 77.59, "Indiranagar");
        repository.save(vehicle);

        Optional<Vehicle> found = repository.findById("V1");
        assertThat(found).isPresent();
        assertThat(found.get().getCurrentAddress()).isEqualTo("Indiranagar");
    }

    @Test
    void testUpsertBehavior() {
        Vehicle vehicle1 = new Vehicle("V1", 100, 12.97, 77.59, "Old Depot");
        repository.save(vehicle1);

        Vehicle updated = new Vehicle("V1", 120, 12.97, 77.59, "New Depot");
        repository.save(updated);

        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findById("V1").get().getCurrentAddress()).isEqualTo("New Depot");
        assertThat(repository.findById("V1").get().getCapacity()).isEqualTo(120.0);
    }

    @Test
    void testSaveNullVehicleThrowsException() {
        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> repository.save(new Vehicle(null, 50, 0, 0, "Test")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testSaveAllAndFindAll() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("V1", 100, 10, 20, "D1"),
                new Vehicle("V2", 80, 11, 21, "D2")
        );
        repository.saveAll(vehicles);

        assertThat(repository.count()).isEqualTo(2);
        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void testDeleteById() {
        Vehicle vehicle = new Vehicle("V1", 100, 12.97, 77.59, "Indiranagar");
        repository.save(vehicle);

        boolean deleted = repository.deleteById("V1");
        assertThat(deleted).isTrue();
        assertThat(repository.findById("V1")).isEmpty();

        boolean deletedAgain = repository.deleteById("V1");
        assertThat(deletedAgain).isFalse();
    }

    @Test
    void testNullKeyHandling() {
        assertThat(repository.findById(null)).isEmpty();
        assertThat(repository.deleteById(null)).isFalse();
    }

    @Test
    void testExistsById() {
        Vehicle vehicle = new Vehicle("V1", 100, 12.97, 77.59, "Indiranagar");
        repository.save(vehicle);

        assertThat(repository.existsById("V1")).isTrue();
        assertThat(repository.existsById("NON_EXISTENT")).isFalse();
        assertThat(repository.existsById(null)).isFalse();
    }

    @Test
    void testSaveAllWithDuplicatesInBatch() {
        List<Vehicle> vehiclesWithDuplicates = List.of(
                new Vehicle("V1", 100, 10, 20, "Old Depot"),
                new Vehicle("V2", 80, 11, 21, "D2"),
                new Vehicle("V1", 150, 10, 20, "Updated Depot")
        );
        repository.saveAll(vehiclesWithDuplicates);

        assertThat(repository.count()).isEqualTo(2);
        Vehicle v1 = repository.findById("V1").orElseThrow();
        assertThat(v1.getCurrentAddress()).isEqualTo("Updated Depot");
        assertThat(v1.getCapacity()).isEqualTo(150.0);
    }

    @Test
    void testSaveAllNullOrEmpty() {
        repository.saveAll(null);
        repository.saveAll(List.of());
        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    void testSaveAllWithInvalidElementThrowsException() {
        List<Vehicle> vehicles = new java.util.ArrayList<>();
        vehicles.add(new Vehicle("V1", 100, 10, 20, "D1"));
        vehicles.add(null);

        assertThatThrownBy(() -> repository.saveAll(vehicles))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testConcurrentBatchUpserts() {
        java.util.stream.IntStream.range(0, 100).parallel().forEach(i -> {
            String id = "VEH-" + (i % 10);
            repository.save(new Vehicle(id, 100.0 + i, 12.0 + i, 77.0 + i, "Depot " + i));
        });

        assertThat(repository.count()).isEqualTo(10);
        for (int i = 0; i < 10; i++) {
            assertThat(repository.existsById("VEH-" + i)).isTrue();
        }
    }

    @Test
    void testDeleteAll() {
        repository.save(new Vehicle("V1", 100, 10, 20, "D1"));
        repository.deleteAll();
        assertThat(repository.count()).isEqualTo(0);
        assertThat(repository.findAll()).isEmpty();
    }
}
