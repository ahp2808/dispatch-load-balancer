package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.VehicleBatchRequest;
import com.freightfox.dispatch.model.Vehicle;
import com.freightfox.dispatch.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void saveVehicles(VehicleBatchRequest request) {
        if (request == null || request.getVehicles() == null) {
            return;
        }

        List<Vehicle> vehicles = request.getVehicles().stream()
                .map(req -> new Vehicle(
                        req.getVehicleId().trim(),
                        req.getCapacity(),
                        req.getCurrentLatitude(),
                        req.getCurrentLongitude(),
                        req.getCurrentAddress().trim()
                ))
                .toList();

        vehicleRepository.saveAll(vehicles);
        log.info("Saved {} vehicles. Total vehicles in store: {}", vehicles.size(), vehicleRepository.count());
    }

    public Collection<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(String vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    public boolean existsVehicle(String vehicleId) {
        return vehicleRepository.existsById(vehicleId);
    }

    public boolean deleteVehicle(String vehicleId) {
        return vehicleRepository.deleteById(vehicleId);
    }

    public void clearAll() {
        vehicleRepository.deleteAll();
    }
}
