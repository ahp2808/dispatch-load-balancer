package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.VehicleBatchRequest;
import com.freightfox.dispatch.dto.VehicleRequest;
import com.freightfox.dispatch.model.Vehicle;
import com.freightfox.dispatch.repository.VehicleRepository;
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
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        vehicleService = new VehicleService(vehicleRepository);
    }

    @Test
    void testSaveVehiclesBatch() {
        VehicleRequest req = new VehicleRequest("V1", 100.0, 12.97, 77.59, "Depot A");
        VehicleBatchRequest batch = new VehicleBatchRequest(List.of(req));

        vehicleService.saveVehicles(batch);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Vehicle>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(vehicleRepository, times(1)).saveAll(captor.capture());

        Collection<Vehicle> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        Vehicle vehicle = saved.iterator().next();
        assertThat(vehicle.getVehicleId()).isEqualTo("V1");
        assertThat(vehicle.getCapacity()).isEqualTo(100.0);
    }

    @Test
    void testSaveVehiclesNullRequest() {
        vehicleService.saveVehicles(null);
        verify(vehicleRepository, never()).saveAll(any());

        vehicleService.saveVehicles(new VehicleBatchRequest(null));
        verify(vehicleRepository, never()).saveAll(any());
    }

    @Test
    void testGetAllVehicles() {
        Vehicle v1 = new Vehicle("V1", 100, 10, 20, "D1");
        when(vehicleRepository.findAll()).thenReturn(List.of(v1));

        Collection<Vehicle> result = vehicleService.getAllVehicles();
        assertThat(result).containsExactly(v1);
    }

    @Test
    void testGetVehicleById() {
        Vehicle v1 = new Vehicle("V1", 100, 10, 20, "D1");
        when(vehicleRepository.findById("V1")).thenReturn(Optional.of(v1));

        Optional<Vehicle> found = vehicleService.getVehicleById("V1");
        assertThat(found).isPresent().contains(v1);
    }

    @Test
    void testExistsVehicle() {
        when(vehicleRepository.existsById("V1")).thenReturn(true);
        when(vehicleRepository.existsById("UNKNOWN")).thenReturn(false);

        assertThat(vehicleService.existsVehicle("V1")).isTrue();
        assertThat(vehicleService.existsVehicle("UNKNOWN")).isFalse();
    }

    @Test
    void testDeleteVehicle() {
        when(vehicleRepository.deleteById("V1")).thenReturn(true);
        boolean deleted = vehicleService.deleteVehicle("V1");
        assertThat(deleted).isTrue();
        verify(vehicleRepository, times(1)).deleteById("V1");
    }

    @Test
    void testClearAll() {
        vehicleService.clearAll();
        verify(vehicleRepository, times(1)).deleteAll();
    }
}
