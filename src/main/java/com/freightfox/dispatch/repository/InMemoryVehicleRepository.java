package com.freightfox.dispatch.repository;

import com.freightfox.dispatch.model.Vehicle;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryVehicleRepository implements VehicleRepository {

    private final Map<String, Vehicle> vehicleMap = new ConcurrentHashMap<>();

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleId() == null) {
            throw new IllegalArgumentException("Vehicle and vehicleId cannot be null");
        }
        vehicleMap.put(vehicle.getVehicleId(), vehicle);
        return vehicle;
    }

    @Override
    public void saveAll(Collection<Vehicle> vehicles) {
        if (vehicles != null) {
            for (Vehicle vehicle : vehicles) {
                save(vehicle);
            }
        }
    }

    @Override
    public Optional<Vehicle> findById(String vehicleId) {
        if (vehicleId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(vehicleMap.get(vehicleId));
    }

    @Override
    public boolean existsById(String vehicleId) {
        return vehicleId != null && vehicleMap.containsKey(vehicleId);
    }

    @Override
    public Collection<Vehicle> findAll() {
        return new ArrayList<>(vehicleMap.values());
    }

    @Override
    public int count() {
        return vehicleMap.size();
    }

    @Override
    public boolean deleteById(String vehicleId) {
        if (vehicleId == null) {
            return false;
        }
        return vehicleMap.remove(vehicleId) != null;
    }

    @Override
    public void deleteAll() {
        vehicleMap.clear();
    }
}
