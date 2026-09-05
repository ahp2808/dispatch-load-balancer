package com.freightfox.dispatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class VehicleBatchRequest {

    @NotNull(message = "vehicles list is required")
    @NotEmpty(message = "vehicles list cannot be empty")
    @Valid
    private List<VehicleRequest> vehicles;

    public VehicleBatchRequest() {
    }

    public VehicleBatchRequest(List<VehicleRequest> vehicles) {
        this.vehicles = vehicles;
    }

    public List<VehicleRequest> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehicleRequest> vehicles) {
        this.vehicles = vehicles;
    }
}
