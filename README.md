# Dispatch Load Balancer

A Spring Boot application that assigns delivery orders to a fleet of vehicles based on vehicle capacity limits, priority levels, and travel distance minimization using the Haversine formula.

---

## Overview & Features

- **Priority Scheduling**: Orders are processed by priority tiers (`HIGH` > `MEDIUM` > `LOW`). Higher priority orders are always allocated before lower priority orders.
- **Capacity Limits**: Vehicles are never assigned loads that exceed their maximum capacity.
- **Distance Minimization**: Uses the great-circle Haversine formula (Earth radius 6,371 km) to calculate distances between coordinates.
- **Route Sequencing**: Assigned stops for each vehicle are sequenced from its starting location using a nearest-neighbor approach.
- **In-Memory Store with Upserts**: Stores orders and vehicles in thread-safe concurrent repositories. Submitting an existing `orderId` or `vehicleId` updates the record rather than duplicating it.
- **Validation & Error Handling**: Request payloads are validated via Jakarta Validation. Validation failures return a `400 Bad Request` with specific field errors.
- **Unassigned Orders**: Any orders that cannot fit into the available fleet are returned in `unassignedOrders` in the dispatch plan response.

---

## How It Works

The dispatch engine assigns orders to vehicles in two phases:

1. **Order Assignment (Greedy Allocation)**
   - Orders are grouped into priority queues: `HIGH`, then `MEDIUM`, then `LOW`.
   - For each order, the service finds all vehicles with sufficient remaining capacity (`vehicle.remainingCapacity >= order.packageWeight`).
   - The order is assigned to the vehicle that has the shortest distance from its current location (or last assigned delivery point).
   - If multiple vehicles are at the same distance, ties are broken deterministically by `vehicleId`.
   - If no vehicle has enough capacity, the order is flagged as unassigned.

2. **Route Sequencing (Nearest-Neighbor)**
   - Once assignments are complete, each vehicle's route is sequenced starting from its base location.
   - At each step, the next nearest unvisited order location is chosen until all assigned orders are visited.
   - Cumulative route distance is computed across all stops.

---

## Project Structure

```
src/main/java/com/freightfox/dispatch/
├── controller/
│   └── DispatchController.java       # REST endpoints
├── service/
│   ├── DispatchService.java          # Assignment logic & route sequencing
│   ├── OrderService.java             # Order management & validation
│   └── VehicleService.java           # Vehicle management & validation
├── repository/
│   ├── OrderRepository.java          # Order repository interface
│   ├── InMemoryOrderRepository.java  # Thread-safe in-memory store
│   ├── VehicleRepository.java        # Vehicle repository interface
│   └── InMemoryVehicleRepository.java# Thread-safe in-memory store
├── model/
│   ├── DeliveryOrder.java            # Order entity
│   ├── Vehicle.java                  # Vehicle entity
│   └── Priority.java                 # HIGH, MEDIUM, LOW enum
├── dto/                              # Request/response DTOs
├── exception/
│   └── GlobalExceptionHandler.java   # Centralized 400/404/500 handler
└── util/
    └── HaversineUtil.java            # Great-circle distance calculations
```

---

## Getting Started

### Prerequisites
- **Java 17** or higher
- Maven (or use the included `./mvnw` wrapper)

### Build & Run
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

The service runs at `http://localhost:8080`. Visiting `http://localhost:8080` in a browser opens the status dashboard.

### Run Tests
```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

---

## API Endpoints

### Summary

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/dispatch/vehicles` | Batch ingest / upsert vehicles |
| `GET` | `/api/dispatch/vehicles` | List all vehicles |
| `GET` | `/api/dispatch/vehicles/{vehicleId}` | Get vehicle by ID |
| `DELETE` | `/api/dispatch/vehicles/{vehicleId}` | Delete vehicle by ID |
| `DELETE` | `/api/dispatch/vehicles` | Clear all vehicles |
| `POST` | `/api/dispatch/orders` | Batch ingest / upsert delivery orders |
| `GET` | `/api/dispatch/orders` | List all orders |
| `GET` | `/api/dispatch/orders/{orderId}` | Get order by ID |
| `DELETE` | `/api/dispatch/orders/{orderId}` | Delete order by ID |
| `DELETE` | `/api/dispatch/orders` | Clear all orders |
| `GET` | `/api/dispatch/plan` | Generate and retrieve the dispatch plan |

---

### Request & Response Examples

#### 1. Add Vehicles (`POST /api/dispatch/vehicles`)

```bash
curl -X POST http://localhost:8080/api/dispatch/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "vehicles": [
      {
        "vehicleId": "VEH001",
        "capacity": 100,
        "currentLatitude": 28.6517,
        "currentLongitude": 77.1906,
        "currentAddress": "Karol Bagh, Delhi"
      },
      {
        "vehicleId": "VEH002",
        "capacity": 80,
        "currentLatitude": 28.5708,
        "currentLongitude": 77.3260,
        "currentAddress": "Sector 18, Noida"
      }
    ]
  }'
```

Response (`200 OK`):
```json
{
  "message": "Vehicle details accepted.",
  "status": "success"
}
```

#### 2. Add Orders (`POST /api/dispatch/orders`)

```bash
curl -X POST http://localhost:8080/api/dispatch/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orders": [
      {
        "orderId": "ORD001",
        "latitude": 28.6510,
        "longitude": 77.1900,
        "address": "Karol Bagh Market, Delhi",
        "packageWeight": 25,
        "priority": "HIGH"
      },
      {
        "orderId": "ORD002",
        "latitude": 28.6320,
        "longitude": 77.2180,
        "address": "Connaught Place, Delhi",
        "packageWeight": 30,
        "priority": "MEDIUM"
      }
    ]
  }'
```

Response (`200 OK`):
```json
{
  "message": "Delivery orders accepted.",
  "status": "success"
}
```

#### 3. Get Dispatch Plan (`GET /api/dispatch/plan`)

```bash
curl http://localhost:8080/api/dispatch/plan
```

Response (`200 OK`):
```json
{
  "dispatchPlan": [
    {
      "vehicleId": "VEH001",
      "totalLoad": 55.0,
      "totalDistance": "3.8 km",
      "assignedOrders": [
        {
          "orderId": "ORD001",
          "latitude": 28.651,
          "longitude": 77.19,
          "address": "Karol Bagh Market, Delhi",
          "packageWeight": 25.0,
          "priority": "HIGH"
        },
        {
          "orderId": "ORD002",
          "latitude": 28.632,
          "longitude": 77.218,
          "address": "Connaught Place, Delhi",
          "packageWeight": 30.0,
          "priority": "MEDIUM"
        }
      ]
    },
    {
      "vehicleId": "VEH002",
      "totalLoad": 0.0,
      "totalDistance": "0 km",
      "assignedOrders": []
    }
  ],
  "unassignedOrders": []
}
```

---

## Testing & Sample Scripts

Several helper scripts are included for testing different scenarios against a running server:

- **`.\load_sample_data.ps1`**: Loads the standard benchmark dataset (5 vehicles, 30 orders across Delhi NCR).
- **`.\load_multistate_sample_data.ps1`**: Loads multi-region fleet and order data (Maharashtra and Delhi NCR) to test regional dispatch separation.
- **`.\load_large_dataset.ps1`**: Generates configurable synthetic workloads (e.g. 50+ vehicles, 300+ orders) across 5 major logistics hubs to benchmark throughput and route sequencing.
  ```powershell
  .\load_large_dataset.ps1 -VehicleCount 50 -OrderCount 300
  ```
