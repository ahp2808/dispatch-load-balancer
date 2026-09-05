package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.DispatchPlanEntry;
import com.freightfox.dispatch.dto.DispatchPlanResponse;
import com.freightfox.dispatch.model.DeliveryOrder;
import com.freightfox.dispatch.model.Priority;
import com.freightfox.dispatch.model.Vehicle;
import com.freightfox.dispatch.repository.OrderRepository;
import com.freightfox.dispatch.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    private DispatchService dispatchService;

    @BeforeEach
    void setUp() {
        dispatchService = new DispatchService(orderRepository, vehicleRepository);
    }

    @Test
    void testEmptyOrdersList() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("VEH001", 100, 12.9716, 77.5946, "MG Road")
        );

        DispatchPlanResponse response = dispatchService.generatePlan(List.of(), vehicles);

        assertThat(response).isNotNull();
        assertThat(response.getDispatchPlan()).hasSize(1);
        assertThat(response.getDispatchPlan().get(0).getVehicleId()).isEqualTo("VEH001");
        assertThat(response.getDispatchPlan().get(0).getTotalLoad()).isEqualTo(0.0);
        assertThat(response.getDispatchPlan().get(0).getTotalDistance()).isEqualTo("0 km");
        assertThat(response.getDispatchPlan().get(0).getAssignedOrders()).isEmpty();
        assertThat(response.getUnassignedOrders()).isEmpty();
    }

    @Test
    void testEmptyVehicleList() {
        List<DeliveryOrder> orders = List.of(
                new DeliveryOrder("ORD001", 12.9716, 77.5946, "MG Road", 20, Priority.HIGH)
        );

        DispatchPlanResponse response = dispatchService.generatePlan(orders, List.of());

        assertThat(response).isNotNull();
        assertThat(response.getDispatchPlan()).isEmpty();
        assertThat(response.getUnassignedOrders()).hasSize(1);
        assertThat(response.getUnassignedOrders().get(0).getOrderId()).isEqualTo("ORD001");
    }

    @Test
    void testOrderExceedingCapacity() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("VEH001", 50, 12.9716, 77.5946, "MG Road")
        );
        List<DeliveryOrder> orders = List.of(
                new DeliveryOrder("ORD001", 12.9716, 77.5946, "MG Road", 100, Priority.HIGH)
        );

        DispatchPlanResponse response = dispatchService.generatePlan(orders, vehicles);

        assertThat(response.getDispatchPlan().get(0).getAssignedOrders()).isEmpty();
        assertThat(response.getUnassignedOrders()).hasSize(1);
        assertThat(response.getUnassignedOrders().get(0).getOrderId()).isEqualTo("ORD001");
    }

    @Test
    void testOrderExactlyMatchingCapacity() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("VEH001", 50, 12.9716, 77.5946, "MG Road")
        );
        List<DeliveryOrder> orders = List.of(
                new DeliveryOrder("ORD001", 12.9716, 77.5946, "MG Road", 50, Priority.HIGH)
        );

        DispatchPlanResponse response = dispatchService.generatePlan(orders, vehicles);

        assertThat(response.getDispatchPlan().get(0).getAssignedOrders()).hasSize(1);
        assertThat(response.getDispatchPlan().get(0).getTotalLoad()).isEqualTo(50.0);
        assertThat(response.getUnassignedOrders()).isEmpty();
    }

    @Test
    void testPriorityOrderingConstraint() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("VEH001", 30, 12.9716, 77.5946, "MG Road")
        );
        List<DeliveryOrder> orders = List.of(
                new DeliveryOrder("ORD_LOW", 12.9716, 77.5946, "MG Road", 20, Priority.LOW),
                new DeliveryOrder("ORD_HIGH", 12.9716, 77.5946, "MG Road", 20, Priority.HIGH)
        );

        DispatchPlanResponse response = dispatchService.generatePlan(orders, vehicles);

        DispatchPlanEntry vehicleEntry = response.getDispatchPlan().get(0);
        assertThat(vehicleEntry.getAssignedOrders()).hasSize(1);
        assertThat(vehicleEntry.getAssignedOrders().get(0).getOrderId()).isEqualTo("ORD_HIGH");
        assertThat(response.getUnassignedOrders()).hasSize(1);
        assertThat(response.getUnassignedOrders().get(0).getOrderId()).isEqualTo("ORD_LOW");
    }

    @Test
    void testDistanceMinimization() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("VEH001", 100, 12.97, 77.59, "Bangalore"),
                new Vehicle("VEH002", 100, 28.61, 77.20, "Delhi")
        );
        List<DeliveryOrder> orders = List.of(
                new DeliveryOrder("ORD001", 12.98, 77.60, "Bangalore East", 10, Priority.HIGH)
        );

        DispatchPlanResponse response = dispatchService.generatePlan(orders, vehicles);

        DispatchPlanEntry veh1 = response.getDispatchPlan().stream()
                .filter(e -> e.getVehicleId().equals("VEH001")).findFirst().orElseThrow();
        DispatchPlanEntry veh2 = response.getDispatchPlan().stream()
                .filter(e -> e.getVehicleId().equals("VEH002")).findFirst().orElseThrow();

        assertThat(veh1.getAssignedOrders()).hasSize(1);
        assertThat(veh2.getAssignedOrders()).isEmpty();
    }

    @Test
    void testDeterminism() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("VEH001", 100, 12.9716, 77.5946, "MG Road"),
                new Vehicle("VEH002", 100, 12.9352, 77.6245, "Koramangala")
        );
        List<DeliveryOrder> orders = List.of(
                new DeliveryOrder("ORD001", 12.9716, 77.5946, "MG Road", 10, Priority.HIGH),
                new DeliveryOrder("ORD002", 12.9352, 77.6245, "Koramangala", 15, Priority.HIGH),
                new DeliveryOrder("ORD003", 12.9784, 77.6408, "Indiranagar", 20, Priority.MEDIUM)
        );

        DispatchPlanResponse plan1 = dispatchService.generatePlan(orders, vehicles);
        DispatchPlanResponse plan2 = dispatchService.generatePlan(orders, vehicles);

        assertThat(plan1.getDispatchPlan()).hasSize(plan2.getDispatchPlan().size());
        for (int i = 0; i < plan1.getDispatchPlan().size(); i++) {
            DispatchPlanEntry e1 = plan1.getDispatchPlan().get(i);
            DispatchPlanEntry e2 = plan2.getDispatchPlan().get(i);
            assertThat(e1.getVehicleId()).isEqualTo(e2.getVehicleId());
            assertThat(e1.getTotalLoad()).isEqualTo(e2.getTotalLoad());
            assertThat(e1.getTotalDistance()).isEqualTo(e2.getTotalDistance());
            assertThat(e1.getAssignedOrders().size()).isEqualTo(e2.getAssignedOrders().size());
        }
    }

    @Test
    void testAllOrdersSamePriorityDeterministicTieBreaking() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("VEH001", 50, 12.9716, 77.5946, "MG Road"),
                new Vehicle("VEH002", 50, 12.9716, 77.5946, "MG Road")
        );
        List<DeliveryOrder> orders = List.of(
                new DeliveryOrder("ORD003", 12.9720, 77.5950, "Stop 3", 20, Priority.HIGH),
                new DeliveryOrder("ORD001", 12.9720, 77.5950, "Stop 1", 20, Priority.HIGH),
                new DeliveryOrder("ORD002", 12.9720, 77.5950, "Stop 2", 20, Priority.HIGH)
        );

        DispatchPlanResponse response1 = dispatchService.generatePlan(orders, vehicles);
        DispatchPlanResponse response2 = dispatchService.generatePlan(orders, vehicles);

        assertThat(response1.getDispatchPlan()).hasSize(2);
        for (int i = 0; i < response1.getDispatchPlan().size(); i++) {
            DispatchPlanEntry e1 = response1.getDispatchPlan().get(i);
            DispatchPlanEntry e2 = response2.getDispatchPlan().get(i);
            assertThat(e1.getVehicleId()).isEqualTo(e2.getVehicleId());
            assertThat(e1.getTotalLoad()).isEqualTo(e2.getTotalLoad());
            assertThat(e1.getAssignedOrders().stream().map(DeliveryOrder::getOrderId).toList())
                    .isEqualTo(e2.getAssignedOrders().stream().map(DeliveryOrder::getOrderId).toList());
        }
    }

    @Test
    void testExcessDemandOverFleetCapacity() {
        List<Vehicle> vehicles = List.of(
                new Vehicle("VEH001", 40, 12.9716, 77.5946, "MG Road")
        );
        List<DeliveryOrder> orders = List.of(
                new DeliveryOrder("ORD_H1", 12.9720, 77.5950, "Stop 1", 20, Priority.HIGH),
                new DeliveryOrder("ORD_H2", 12.9730, 77.5960, "Stop 2", 20, Priority.HIGH),
                new DeliveryOrder("ORD_M1", 12.9740, 77.5970, "Stop 3", 15, Priority.MEDIUM),
                new DeliveryOrder("ORD_L1", 12.9750, 77.5980, "Stop 4", 15, Priority.LOW)
        );

        DispatchPlanResponse response = dispatchService.generatePlan(orders, vehicles);

        DispatchPlanEntry entry = response.getDispatchPlan().get(0);
        assertThat(entry.getTotalLoad()).isLessThanOrEqualTo(40.0);
        assertThat(entry.getTotalLoad()).isEqualTo(40.0);

        assertThat(entry.getAssignedOrders().stream().map(DeliveryOrder::getOrderId).toList())
                .containsExactlyInAnyOrder("ORD_H1", "ORD_H2");

        assertThat(response.getUnassignedOrders().stream().map(DeliveryOrder::getOrderId).toList())
                .containsExactly("ORD_L1", "ORD_M1");
    }
}
