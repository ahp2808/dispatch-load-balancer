package com.freightfox.dispatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrderBatchRequest {

    @NotNull(message = "orders list is required")
    @NotEmpty(message = "orders list cannot be empty")
    @Valid
    private List<OrderRequest> orders;

    public OrderBatchRequest() {
    }

    public OrderBatchRequest(List<OrderRequest> orders) {
        this.orders = orders;
    }

    public List<OrderRequest> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderRequest> orders) {
        this.orders = orders;
    }
}
