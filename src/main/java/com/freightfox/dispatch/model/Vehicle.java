package com.freightfox.dispatch.model;

import java.util.Objects;

public class Vehicle {

    private String vehicleId;
    private double capacity;
    private double currentLatitude;
    private double currentLongitude;
    private String currentAddress;

    public Vehicle() {
    }

    public Vehicle(String vehicleId, double capacity, double currentLatitude, double currentLongitude, String currentAddress) {
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

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(vehicleId, vehicle.vehicleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId='" + vehicleId + '\'' +
                ", capacity=" + capacity +
                ", currentLatitude=" + currentLatitude +
                ", currentLongitude=" + currentLongitude +
                ", currentAddress='" + currentAddress + '\'' +
                '}';
    }
}
