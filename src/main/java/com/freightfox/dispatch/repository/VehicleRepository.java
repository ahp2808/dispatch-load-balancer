package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.model.Vehicle;
import java.util.Collection;
import java.util.Optional;

public interface VehicleRepository {

    Vehicle save(Vehicle vehicle);

    void saveAll(Collection<Vehicle> vehicles);

    Optional<Vehicle> findById(String vehicleId);

    boolean existsById(String vehicleId);

    Collection<Vehicle> findAll();

    int count();

    boolean deleteById(String vehicleId);

    void deleteAll();
}
