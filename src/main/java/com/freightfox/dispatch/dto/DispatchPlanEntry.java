package com.freightfox.dispatch.dto;

import com.freightfox.dispatch.model.DeliveryOrder;
import java.util.ArrayList;
import java.util.List;

public class DispatchPlanEntry {

    private String vehicleId;
    private double totalLoad;
    private String totalDistance;
    private List<DeliveryOrder> assignedOrders;

    public DispatchPlanEntry() {
        this.assignedOrders = new ArrayList<>();
    }

    public DispatchPlanEntry(String vehicleId, double totalLoad, String totalDistance, List<DeliveryOrder> assignedOrders) {
        this.vehicleId = vehicleId;
        this.totalLoad = totalLoad;
        this.totalDistance = totalDistance;
        this.assignedOrders = assignedOrders != null ? assignedOrders : new ArrayList<>();
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getTotalLoad() {
        return totalLoad;
    }

    public void setTotalLoad(double totalLoad) {
        this.totalLoad = totalLoad;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public List<DeliveryOrder> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<DeliveryOrder> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
}
