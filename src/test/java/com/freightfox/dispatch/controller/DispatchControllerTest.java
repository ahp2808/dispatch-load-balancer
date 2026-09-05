package com.freightfox.dispatch.controller;

import com.freightfox.dispatch.repository.OrderRepository;
import com.freightfox.dispatch.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DispatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void cleanRepositories() {
        orderRepository.deleteAll();
        vehicleRepository.deleteAll();
    }

    @Test
    void testAcceptOrdersValid() throws Exception {
        String json = """
                {
                  "orders": [
                    {
                      "orderId": "ORD001",
                      "latitude": 12.9716,
                      "longitude": 77.5946,
                      "address": "MG Road, Bangalore, Karnataka, India",
                      "packageWeight": 10,
                      "priority": "HIGH"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Delivery orders accepted."));
    }

    @Test
    void testAcceptOrdersInvalidPriority() throws Exception {
        String json = """
                {
                  "orders": [
                    {
                      "orderId": "ORD001",
                      "latitude": 12.9716,
                      "longitude": 77.5946,
                      "address": "MG Road, Bangalore",
                      "packageWeight": 10,
                      "priority": "URGENT"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void testAcceptOrdersNonPositiveWeight() throws Exception {
        String json = """
                {
                  "orders": [
                    {
                      "orderId": "ORD001",
                      "latitude": 12.9716,
                      "longitude": 77.5946,
                      "address": "MG Road, Bangalore",
                      "packageWeight": -5,
                      "priority": "HIGH"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors", hasItem(containsString("packageWeight"))));
    }

    @Test
    void testAcceptOrdersInvalidCoordinates() throws Exception {
        String json = """
                {
                  "orders": [
                    {
                      "orderId": "ORD001",
                      "latitude": 95.0,
                      "longitude": 77.5946,
                      "address": "MG Road, Bangalore",
                      "packageWeight": 10,
                      "priority": "HIGH"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors", hasItem(containsString("latitude"))));
    }

    @Test
    void testAcceptOrdersMissingFields() throws Exception {
        String json = """
                {
                  "orders": [
                    {
                      "latitude": 12.9716,
                      "longitude": 77.5946
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors", hasItem(containsString("orderId"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("address"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("packageWeight"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("priority"))));
    }

    @Test
    void testMalformedJson() throws Exception {
        String invalidJson = "{ orders: [ invalid json }";

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void testAcceptVehiclesValid() throws Exception {
        String json = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH001",
                      "capacity": 100,
                      "currentLatitude": 12.9716,
                      "currentLongitude": 77.6413,
                      "currentAddress": "Indiranagar, Bangalore, Karnataka, India"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Vehicle details accepted."));
    }

    @Test
    void testAcceptVehiclesNonPositiveCapacity() throws Exception {
        String json = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH001",
                      "capacity": 0,
                      "currentLatitude": 12.9716,
                      "currentLongitude": 77.6413,
                      "currentAddress": "Indiranagar, Bangalore"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors", hasItem(containsString("capacity"))));
    }

    @Test
    void testAcceptVehiclesInvalidCoordinates() throws Exception {
        String json = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH001",
                      "capacity": 100,
                      "currentLatitude": 12.9716,
                      "currentLongitude": 185.0,
                      "currentAddress": "Indiranagar, Bangalore"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors", hasItem(containsString("currentLongitude"))));
    }

    @Test
    void testAcceptVehiclesMissingFields() throws Exception {
        String json = """
                {
                  "vehicles": [
                    {
                      "capacity": 100
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.errors", hasItem(containsString("vehicleId"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("currentLatitude"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("currentLongitude"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("currentAddress"))));
    }

    @Test
    void testGetDispatchPlanEmpty() throws Exception {
        mockMvc.perform(get("/api/dispatch/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchPlan").isArray())
                .andExpect(jsonPath("$.unassignedOrders").isArray());
    }

    @Test
    void testOrderUpsertSemantics() throws Exception {
        String jsonInitial = """
                {
                  "orders": [
                    {
                      "orderId": "ORD001",
                      "latitude": 12.9716,
                      "longitude": 77.5946,
                      "address": "Old Address",
                      "packageWeight": 10,
                      "priority": "LOW"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInitial))
                .andExpect(status().isOk());

        String jsonUpdate = """
                {
                  "orders": [
                    {
                      "orderId": "ORD001",
                      "latitude": 12.9716,
                      "longitude": 77.5946,
                      "address": "Updated Address",
                      "packageWeight": 15,
                      "priority": "HIGH"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate))
                .andExpect(status().isOk());

        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(orderRepository.findById("ORD001").orElseThrow().getAddress()).isEqualTo("Updated Address");
        assertThat(orderRepository.findById("ORD001").orElseThrow().getPriority().name()).isEqualTo("HIGH");
        assertThat(orderRepository.findById("ORD001").orElseThrow().getPackageWeight()).isEqualTo(15.0);
    }

    @Test
    void testVehicleUpsertSemantics() throws Exception {
        String jsonInitial = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH001",
                      "capacity": 80,
                      "currentLatitude": 12.9716,
                      "currentLongitude": 77.6413,
                      "currentAddress": "Old Location"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInitial))
                .andExpect(status().isOk());

        String jsonUpdate = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH001",
                      "capacity": 150,
                      "currentLatitude": 12.9720,
                      "currentLongitude": 77.6420,
                      "currentAddress": "Updated Location"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate))
                .andExpect(status().isOk());

        assertThat(vehicleRepository.count()).isEqualTo(1);
        assertThat(vehicleRepository.findById("VEH001").orElseThrow().getCurrentAddress()).isEqualTo("Updated Location");
        assertThat(vehicleRepository.findById("VEH001").orElseThrow().getCapacity()).isEqualTo(150.0);
    }

    @Test
    void testEndToEndDispatchFlow() throws Exception {
        String vehicleJson = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH001",
                      "capacity": 50,
                      "currentLatitude": 12.9716,
                      "currentLongitude": 77.5946,
                      "currentAddress": "MG Road, Bangalore"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson))
                .andExpect(status().isOk());

        String orderJson = """
                {
                  "orders": [
                    {
                      "orderId": "ORD001",
                      "latitude": 12.9720,
                      "longitude": 77.5950,
                      "address": "Brigade Road, Bangalore",
                      "packageWeight": 20,
                      "priority": "HIGH"
                    },
                    {
                      "orderId": "ORD002",
                      "latitude": 12.9730,
                      "longitude": 77.5960,
                      "address": "Church Street, Bangalore",
                      "packageWeight": 40,
                      "priority": "LOW"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dispatch/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchPlan", hasSize(1)))
                .andExpect(jsonPath("$.dispatchPlan[0].vehicleId").value("VEH001"))
                .andExpect(jsonPath("$.dispatchPlan[0].totalLoad").value(20.0))
                .andExpect(jsonPath("$.dispatchPlan[0].assignedOrders", hasSize(1)))
                .andExpect(jsonPath("$.dispatchPlan[0].assignedOrders[0].orderId").value("ORD001"))
                .andExpect(jsonPath("$.unassignedOrders", hasSize(1)))
                .andExpect(jsonPath("$.unassignedOrders[0].orderId").value("ORD002"));
    }

    @Test
    void testRootEndpointHtml() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Dispatch Load Balancer API")))
                .andExpect(content().string(containsString("/api/dispatch/plan")));
    }

    @Test
    void testRootEndpointJson() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("Dispatch Load Balancer REST API"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.endpoints['GET /api/dispatch/plan']").isNotEmpty());
    }

    @Test
    void testUnsupportedMediaTypeReturns415() throws Exception {
        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hello=world"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("Unsupported Content-Type")));
    }

    @Test
    void testMethodNotAllowedReturns405() throws Exception {
        mockMvc.perform(post("/api/dispatch/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("Request method 'POST' is not supported")));
    }

    @Test
    void testGetOrdersAndGetById() throws Exception {
        String json = """
                {
                  "orders": [
                    {
                      "orderId": "ORD-REST-01",
                      "latitude": 28.6139,
                      "longitude": 77.2090,
                      "address": "Connaught Place, New Delhi",
                      "packageWeight": 15,
                      "priority": "HIGH"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dispatch/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderId").value("ORD-REST-01"));

        mockMvc.perform(get("/api/dispatch/orders/ORD-REST-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ORD-REST-01"))
                .andExpect(jsonPath("$.address").value("Connaught Place, New Delhi"));

        mockMvc.perform(get("/api/dispatch/orders/NON-EXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("Order with id 'NON-EXISTENT' not found")));
    }

    @Test
    void testDeleteOrderById() throws Exception {
        String json = """
                {
                  "orders": [
                    {
                      "orderId": "ORD-DEL-01",
                      "latitude": 28.6139,
                      "longitude": 77.2090,
                      "address": "Connaught Place",
                      "packageWeight": 10,
                      "priority": "LOW"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/dispatch/orders/ORD-DEL-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value(containsString("deleted successfully")));

        assertThat(orderRepository.count()).isEqualTo(0);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/dispatch/orders/ORD-DEL-01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void testClearAllOrders() throws Exception {
        String json = """
                {
                  "orders": [
                    {
                      "orderId": "ORD-1",
                      "latitude": 28.61,
                      "longitude": 77.20,
                      "address": "Delhi",
                      "packageWeight": 10,
                      "priority": "HIGH"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/dispatch/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("All delivery orders cleared."));

        assertThat(orderRepository.count()).isEqualTo(0);
    }

    @Test
    void testGetVehiclesAndGetById() throws Exception {
        String json = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH-REST-01",
                      "capacity": 120,
                      "currentLatitude": 28.65,
                      "currentLongitude": 77.19,
                      "currentAddress": "Karol Bagh, Delhi"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/dispatch/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vehicleId").value("VEH-REST-01"));

        mockMvc.perform(get("/api/dispatch/vehicles/VEH-REST-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value("VEH-REST-01"))
                .andExpect(jsonPath("$.capacity").value(120.0));

        mockMvc.perform(get("/api/dispatch/vehicles/NON-EXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value(containsString("Vehicle with id 'NON-EXISTENT' not found")));
    }

    @Test
    void testDeleteVehicleById() throws Exception {
        String json = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH-DEL-01",
                      "capacity": 100,
                      "currentLatitude": 28.65,
                      "currentLongitude": 77.19,
                      "currentAddress": "Karol Bagh"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/dispatch/vehicles/VEH-DEL-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value(containsString("deleted successfully")));

        assertThat(vehicleRepository.count()).isEqualTo(0);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/dispatch/vehicles/VEH-DEL-01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void testClearAllVehicles() throws Exception {
        String json = """
                {
                  "vehicles": [
                    {
                      "vehicleId": "VEH-1",
                      "capacity": 100,
                      "currentLatitude": 28.65,
                      "currentLongitude": 77.19,
                      "currentAddress": "Karol Bagh"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/dispatch/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("All vehicle details cleared."));

        assertThat(vehicleRepository.count()).isEqualTo(0);
    }
}
