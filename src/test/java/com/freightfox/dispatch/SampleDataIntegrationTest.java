package com.freightfox.dispatch;

import com.freightfox.dispatch.dto.DispatchPlanEntry;
import com.freightfox.dispatch.dto.DispatchPlanResponse;
import com.freightfox.dispatch.model.DeliveryOrder;
import com.freightfox.dispatch.model.Priority;
import com.freightfox.dispatch.model.Vehicle;
import com.freightfox.dispatch.service.DispatchService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SampleDataIntegrationTest {

    private final DispatchService dispatchService = new DispatchService(null, null);

    @Test
    void testSampleDataScenario() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("VEH001", 100, 28.6517, 77.1906, "Karol Bagh, Delhi"),
                new Vehicle("VEH002", 80, 28.5708, 77.3260, "Sector 18, Noida"),
                new Vehicle("VEH003", 120, 28.4950, 77.0895, "Cyber Hub, Gurgaon"),
                new Vehicle("VEH004", 90, 28.6315, 77.2167, "Connaught Place, Delhi"),
                new Vehicle("VEH005", 110, 28.6517, 77.1906, "Karol Bagh, Delhi")
        );

        List<DeliveryOrder> orders = new ArrayList<>();
        orders.add(new DeliveryOrder("ORD001", 28.6510, 77.1900, "Karol Bagh Market, Delhi", 25, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD002", 28.6320, 77.2180, "CP Inner Circle, Delhi", 30, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD003", 28.6550, 77.1880, "Dev Nagar, Karol Bagh, Delhi", 15, Priority.MEDIUM));
        orders.add(new DeliveryOrder("ORD004", 28.6300, 77.2200, "Barakhamba Road, Delhi", 20, Priority.LOW));
        orders.add(new DeliveryOrder("ORD005", 28.6480, 77.1920, "Pusa Road, Delhi", 35, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD006", 28.6340, 77.2150, "Janpath, Delhi", 10, Priority.MEDIUM));
        orders.add(new DeliveryOrder("ORD007", 28.6530, 77.1850, "Rajendra Place, Delhi", 40, Priority.LOW));
        orders.add(new DeliveryOrder("ORD008", 28.6280, 77.2140, "Tolstoy Marg, Delhi", 25, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD009", 28.6490, 77.1890, "Karol Bagh Metro, Delhi", 15, Priority.MEDIUM));
        orders.add(new DeliveryOrder("ORD010", 28.6360, 77.2170, "Kasturba Gandhi Marg, Delhi", 30, Priority.LOW));

        orders.add(new DeliveryOrder("ORD011", 28.5700, 77.3250, "Atta Market, Sector 18, Noida", 20, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD012", 28.5680, 77.3280, "Gautam Buddha Park, Noida", 35, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD013", 28.5720, 77.3240, "Wave Mall, Sector 18, Noida", 15, Priority.MEDIUM));
        orders.add(new DeliveryOrder("ORD014", 28.5740, 77.3200, "Sector 16 Metro, Noida", 25, Priority.LOW));
        orders.add(new DeliveryOrder("ORD015", 28.5660, 77.3300, "Great India Place, Noida", 30, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD016", 28.5750, 77.3220, "Film City, Sector 16A, Noida", 10, Priority.MEDIUM));
        orders.add(new DeliveryOrder("ORD017", 28.5640, 77.3320, "Sector 38A, Noida", 40, Priority.LOW));
        orders.add(new DeliveryOrder("ORD018", 28.5710, 77.3270, "DLF Mall of India, Noida", 20, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD019", 28.5690, 77.3230, "Sector 18 Commercial, Noida", 15, Priority.MEDIUM));
        orders.add(new DeliveryOrder("ORD020", 28.5760, 77.3210, "Sector 15, Noida", 25, Priority.LOW));

        orders.add(new DeliveryOrder("ORD021", 28.4940, 77.0890, "Cyber City Building 10, Gurgaon", 30, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD022", 28.4960, 77.0910, "DLF Phase 2, Gurgaon", 25, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD023", 28.4930, 77.0870, "Cyber Hub Amphitheatre, Gurgaon", 15, Priority.MEDIUM));
        orders.add(new DeliveryOrder("ORD024", 28.4910, 77.0850, "Belvedere Towers, Gurgaon", 35, Priority.LOW));
        orders.add(new DeliveryOrder("ORD025", 28.4970, 77.0920, "DLF Phase 3, Gurgaon", 20, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD026", 28.4920, 77.0880, "Infinity Tower, Gurgaon", 10, Priority.MEDIUM));
        orders.add(new DeliveryOrder("ORD027", 28.4890, 77.0830, "Sikanderpur, Gurgaon", 40, Priority.LOW));
        orders.add(new DeliveryOrder("ORD028", 28.4980, 77.0940, "MG Road Metro, Gurgaon", 25, Priority.HIGH));
        orders.add(new DeliveryOrder("ORD029", 28.4955, 77.0900, "Building 8, Cyber City, Gurgaon", 15, Priority.MEDIUM));
        orders.add(new DeliveryOrder("ORD030", 28.4880, 77.0820, "DLF City Court, Gurgaon", 30, Priority.LOW));

        double totalOrderWeight = orders.stream().mapToDouble(DeliveryOrder::getPackageWeight).sum();
        double totalFleetCapacity = vehicles.stream().mapToDouble(Vehicle::getCapacity).sum();

        assertThat(totalOrderWeight).isGreaterThan(totalFleetCapacity);

        DispatchPlanResponse response = dispatchService.generatePlan(orders, vehicles);

        assertThat(response).isNotNull();
        assertThat(response.getDispatchPlan()).hasSize(5);

        Map<String, Vehicle> vehicleMap = vehicles.stream()
                .collect(Collectors.toMap(Vehicle::getVehicleId, v -> v));

        for (DispatchPlanEntry entry : response.getDispatchPlan()) {
            Vehicle vehicle = vehicleMap.get(entry.getVehicleId());
            assertThat(entry.getTotalLoad())
                    .as("Vehicle %s load must not exceed capacity %f", entry.getVehicleId(), vehicle.getCapacity())
                    .isLessThanOrEqualTo(vehicle.getCapacity());

            double calculatedSum = entry.getAssignedOrders().stream()
                    .mapToDouble(DeliveryOrder::getPackageWeight).sum();
            assertThat(entry.getTotalLoad()).isEqualTo(calculatedSum);
        }

        assertThat(response.getUnassignedOrders()).isNotEmpty();

        boolean anyLowAssigned = response.getDispatchPlan().stream()
                .flatMap(e -> e.getAssignedOrders().stream())
                .anyMatch(o -> o.getPriority() == Priority.LOW);

        boolean anyHighUnassigned = response.getUnassignedOrders().stream()
                .anyMatch(o -> o.getPriority() == Priority.HIGH);

        if (anyHighUnassigned) {
            assertThat(anyLowAssigned)
                    .as("If any HIGH order is unassigned due to capacity, no LOW order should have taken space that could fit it")
                    .isFalse();
        }

        for (DispatchPlanEntry entry : response.getDispatchPlan()) {
            assertThat(entry.getTotalDistance()).endsWith("km");
        }
    }
}
