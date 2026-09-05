package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.DispatchPlanEntry;
import com.freightfox.dispatch.dto.DispatchPlanResponse;
import com.freightfox.dispatch.model.DeliveryOrder;
import com.freightfox.dispatch.model.Vehicle;
import com.freightfox.dispatch.repository.OrderRepository;
import com.freightfox.dispatch.repository.VehicleRepository;
import com.freightfox.dispatch.util.HaversineUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DispatchService {

    private static final Logger log = LoggerFactory.getLogger(DispatchService.class);
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;

    public DispatchService(OrderRepository orderRepository, VehicleRepository vehicleRepository) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public DispatchPlanResponse computeDispatchPlan() {
        return generatePlan(
                new ArrayList<>(orderRepository.findAll()),
                new ArrayList<>(vehicleRepository.findAll())
        );
    }

    public DispatchPlanResponse generatePlan(List<DeliveryOrder> orders, List<Vehicle> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
            List<DeliveryOrder> unassigned = orders != null ? new ArrayList<>(orders) : new ArrayList<>();
            unassigned.sort(Comparator.comparing(DeliveryOrder::getOrderId));
            return new DispatchPlanResponse(Collections.emptyList(), unassigned);
        }

        if (orders == null || orders.isEmpty()) {
            List<DispatchPlanEntry> emptyEntries = vehicles.stream()
                    .sorted(Comparator.comparing(Vehicle::getVehicleId))
                    .map(v -> new DispatchPlanEntry(v.getVehicleId(), 0.0, "0 km", Collections.emptyList()))
                    .toList();
            return new DispatchPlanResponse(emptyEntries, Collections.emptyList());
        }

        List<VehicleState> vehicleStates = vehicles.stream()
                .sorted(Comparator.comparing(Vehicle::getVehicleId))
                .map(VehicleState::new)
                .toList();

        List<DeliveryOrder> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort(Comparator.comparing(DeliveryOrder::getPriority)
                .thenComparing(DeliveryOrder::getOrderId));

        List<DeliveryOrder> unassignedOrders = new ArrayList<>();

        for (DeliveryOrder order : sortedOrders) {
            VehicleState bestVehicle = null;
            double minDistance = Double.MAX_VALUE;

            for (VehicleState vs : vehicleStates) {
                if (vs.remainingCapacity >= order.getPackageWeight()) {
                    double dist = HaversineUtil.distanceKm(
                            vs.currentLat, vs.currentLon,
                            order.getLatitude(), order.getLongitude()
                    );

                    if (dist < minDistance) {
                        minDistance = dist;
                        bestVehicle = vs;
                    }
                }
            }

            if (bestVehicle != null) {
                bestVehicle.assignedOrders.add(order);
                bestVehicle.remainingCapacity -= order.getPackageWeight();
                bestVehicle.currentLat = order.getLatitude();
                bestVehicle.currentLon = order.getLongitude();
            } else {
                unassignedOrders.add(order);
            }
        }

        List<DispatchPlanEntry> planEntries = new ArrayList<>();
        for (VehicleState vs : vehicleStates) {
            List<DeliveryOrder> sequencedOrders = sequenceRoute(vs.vehicle, vs.assignedOrders);
            double totalDistance = calculateTotalRouteDistance(vs.vehicle, sequencedOrders);
            double totalLoad = Math.round(vs.assignedOrders.stream()
                    .mapToDouble(DeliveryOrder::getPackageWeight).sum() * 100.0) / 100.0;

            planEntries.add(new DispatchPlanEntry(
                    vs.vehicle.getVehicleId(),
                    totalLoad,
                    HaversineUtil.formatDistance(totalDistance),
                    sequencedOrders
            ));
        }

        unassignedOrders.sort(Comparator.comparing(DeliveryOrder::getOrderId));
        if (!unassignedOrders.isEmpty()) {
            log.warn("{} orders could not be assigned due to fleet capacity limits", unassignedOrders.size());
        }

        return new DispatchPlanResponse(planEntries, unassignedOrders);
    }

    private List<DeliveryOrder> sequenceRoute(Vehicle vehicle, List<DeliveryOrder> orders) {
        if (orders == null || orders.size() <= 1) {
            return orders != null ? new ArrayList<>(orders) : new ArrayList<>();
        }

        List<DeliveryOrder> unvisited = new ArrayList<>(orders);
        List<DeliveryOrder> route = new ArrayList<>(orders.size());

        double currentLat = vehicle.getCurrentLatitude();
        double currentLon = vehicle.getCurrentLongitude();

        while (!unvisited.isEmpty()) {
            DeliveryOrder nearest = null;
            double nearestDist = Double.MAX_VALUE;

            for (DeliveryOrder order : unvisited) {
                double d = HaversineUtil.distanceKm(currentLat, currentLon, order.getLatitude(), order.getLongitude());
                if (d < nearestDist) {
                    nearestDist = d;
                    nearest = order;
                }
            }

            if (nearest != null) {
                route.add(nearest);
                unvisited.remove(nearest);
                currentLat = nearest.getLatitude();
                currentLon = nearest.getLongitude();
            } else {
                route.addAll(unvisited);
                break;
            }
        }

        return route;
    }

    private double calculateTotalRouteDistance(Vehicle vehicle, List<DeliveryOrder> route) {
        if (route == null || route.isEmpty()) {
            return 0.0;
        }

        double totalDistance = 0.0;
        double currentLat = vehicle.getCurrentLatitude();
        double currentLon = vehicle.getCurrentLongitude();

        for (DeliveryOrder stop : route) {
            totalDistance += HaversineUtil.distanceKm(currentLat, currentLon, stop.getLatitude(), stop.getLongitude());
            currentLat = stop.getLatitude();
            currentLon = stop.getLongitude();
        }

        return totalDistance;
    }

    private static class VehicleState {
        final Vehicle vehicle;
        double remainingCapacity;
        double currentLat;
        double currentLon;
        final List<DeliveryOrder> assignedOrders = new ArrayList<>();

        VehicleState(Vehicle vehicle) {
            this.vehicle = vehicle;
            this.remainingCapacity = vehicle.getCapacity();
            this.currentLat = vehicle.getCurrentLatitude();
            this.currentLon = vehicle.getCurrentLongitude();
        }
    }
}
