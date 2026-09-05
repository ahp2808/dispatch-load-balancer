package com.freightfox.dispatch.dto;

import com.freightfox.dispatch.model.Priority;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderRequest {

    @NotBlank(message = "orderId is required and cannot be blank")
    private String orderId;

    @NotNull(message = "latitude is required")
    @DecimalMin(value = "-90.0", message = "latitude must be between -90.0 and 90.0")
    @DecimalMax(value = "90.0", message = "latitude must be between -90.0 and 90.0")
    private Double latitude;

    @NotNull(message = "longitude is required")
    @DecimalMin(value = "-180.0", message = "longitude must be between -180.0 and 180.0")
    @DecimalMax(value = "180.0", message = "longitude must be between -180.0 and 180.0")
    private Double longitude;

    @NotBlank(message = "address is required and cannot be blank")
    private String address;

    @NotNull(message = "packageWeight is required")
    @Positive(message = "packageWeight must be greater than 0")
    private Double packageWeight;

    @NotNull(message = "priority is required (must be HIGH, MEDIUM, or LOW)")
    private Priority priority;

    public OrderRequest() {
    }

    public OrderRequest(String orderId, Double latitude, Double longitude, String address, Double packageWeight, Priority priority) {
        this.orderId = orderId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.packageWeight = packageWeight;
        this.priority = priority;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(Double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
