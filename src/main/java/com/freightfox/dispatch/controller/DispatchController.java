package com.freightfox.dispatch.controller;

import com.freightfox.dispatch.dto.ApiResponse;
import com.freightfox.dispatch.dto.DispatchPlanResponse;
import com.freightfox.dispatch.dto.OrderBatchRequest;
import com.freightfox.dispatch.dto.VehicleBatchRequest;
import com.freightfox.dispatch.exception.ResourceNotFoundException;
import com.freightfox.dispatch.model.DeliveryOrder;
import com.freightfox.dispatch.model.Vehicle;
import com.freightfox.dispatch.service.DispatchService;
import com.freightfox.dispatch.service.OrderService;
import com.freightfox.dispatch.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/dispatch", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class DispatchController {

    private final OrderService orderService;
    private final VehicleService vehicleService;
    private final DispatchService dispatchService;

    public DispatchController(OrderService orderService,
                              VehicleService vehicleService,
                              DispatchService dispatchService) {
        this.orderService = orderService;
        this.vehicleService = vehicleService;
        this.dispatchService = dispatchService;
    }

    @PostMapping(value = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> acceptOrders(@Valid @RequestBody OrderBatchRequest request) {
        orderService.saveOrders(request);
        return ResponseEntity.ok(ApiResponse.success("Delivery orders accepted."));
    }

    @GetMapping("/orders")
    public ResponseEntity<Collection<DeliveryOrder>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<DeliveryOrder> getOrderById(@PathVariable String orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id '" + orderId + "' not found"));
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse> deleteOrderById(@PathVariable String orderId) {
        boolean deleted = orderService.deleteOrder(orderId);
        if (!deleted) {
            throw new ResourceNotFoundException("Order with id '" + orderId + "' not found");
        }
        return ResponseEntity.ok(ApiResponse.success("Order '" + orderId + "' deleted successfully."));
    }

    @DeleteMapping("/orders")
    public ResponseEntity<ApiResponse> clearAllOrders() {
        orderService.clearAll();
        return ResponseEntity.ok(ApiResponse.success("All delivery orders cleared."));
    }

    @PostMapping(value = "/vehicles", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> acceptVehicles(@Valid @RequestBody VehicleBatchRequest request) {
        vehicleService.saveVehicles(request);
        return ResponseEntity.ok(ApiResponse.success("Vehicle details accepted."));
    }

    @GetMapping("/vehicles")
    public ResponseEntity<Collection<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/vehicles/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String vehicleId) {
        return vehicleService.getVehicleById(vehicleId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id '" + vehicleId + "' not found"));
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    public ResponseEntity<ApiResponse> deleteVehicleById(@PathVariable String vehicleId) {
        boolean deleted = vehicleService.deleteVehicle(vehicleId);
        if (!deleted) {
            throw new ResourceNotFoundException("Vehicle with id '" + vehicleId + "' not found");
        }
        return ResponseEntity.ok(ApiResponse.success("Vehicle '" + vehicleId + "' deleted successfully."));
    }

    @DeleteMapping("/vehicles")
    public ResponseEntity<ApiResponse> clearAllVehicles() {
        vehicleService.clearAll();
        return ResponseEntity.ok(ApiResponse.success("All vehicle details cleared."));
    }

    @GetMapping("/plan")
    public ResponseEntity<DispatchPlanResponse> getDispatchPlan() {
        DispatchPlanResponse plan = dispatchService.computeDispatchPlan();
        return ResponseEntity.ok(plan);
    }
}
