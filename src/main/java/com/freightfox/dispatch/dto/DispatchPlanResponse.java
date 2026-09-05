package com.freightfox.dispatch.dto;

import com.freightfox.dispatch.model.DeliveryOrder;
import java.util.ArrayList;
import java.util.List;

public class DispatchPlanResponse {

    private List<DispatchPlanEntry> dispatchPlan;
    private List<DeliveryOrder> unassignedOrders;

    public DispatchPlanResponse() {
        this.dispatchPlan = new ArrayList<>();
        this.unassignedOrders = new ArrayList<>();
    }

    public DispatchPlanResponse(List<DispatchPlanEntry> dispatchPlan, List<DeliveryOrder> unassignedOrders) {
        this.dispatchPlan = dispatchPlan != null ? dispatchPlan : new ArrayList<>();
        this.unassignedOrders = unassignedOrders != null ? unassignedOrders : new ArrayList<>();
    }

    public List<DispatchPlanEntry> getDispatchPlan() {
        return dispatchPlan;
    }

    public void setDispatchPlan(List<DispatchPlanEntry> dispatchPlan) {
        this.dispatchPlan = dispatchPlan;
    }

    public List<DeliveryOrder> getUnassignedOrders() {
        return unassignedOrders;
    }

    public void setUnassignedOrders(List<DeliveryOrder> unassignedOrders) {
        this.unassignedOrders = unassignedOrders;
    }
}
