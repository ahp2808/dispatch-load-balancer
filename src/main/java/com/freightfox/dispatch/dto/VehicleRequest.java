package com.freightfox.dispatch.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class VehicleRequest {

    @NotBlank(message = "vehicleId is required and cannot be blank")
    private String vehicleId;

    @NotNull(message = "capacity is required")
    @Positive(message = "capacity must be greater than 0")
    private Double capacity;

    @NotNull(message = "currentLatitude is required")
    @DecimalMin(value = "-90.0", message = "currentLatitude must be between -90.0 and 90.0")
    @DecimalMax(value = "90.0", message = "currentLatitude must be between -90.0 and 90.0")
    private Double currentLatitude;

    @NotNull(message = "currentLongitude is required")
    @DecimalMin(value = "-180.0", message = "currentLongitude must be between -180.0 and 180.0")
    @DecimalMax(value = "180.0", message = "currentLongitude must be between -180.0 and 180.0")
    private Double currentLongitude;

    @NotBlank(message = "currentAddress is required and cannot be blank")
    private String currentAddress;

    public VehicleRequest() {
    }

    public VehicleRequest(String vehicleId, Double capacity, Double currentLatitude, Double currentLongitude, String currentAddress) {
        this.vehicleId = vehicleId;
        this.capacity = capacity;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.currentAddress = currentAddress;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }
}
