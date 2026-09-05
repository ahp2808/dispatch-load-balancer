# Script to load 5 vehicles and 30 sample orders into the running Dispatch Load Balancer
$baseUrl = "http://localhost:8080/api/dispatch"

Write-Host "`nLoading sample fleet and orders..." -ForegroundColor Cyan
Write-Host "Target: $baseUrl`n" -ForegroundColor Gray

# Ingest vehicles
Write-Host "Submitting 5 fleet vehicles..." -ForegroundColor Gray
$vehiclesJson = @"
{
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
    },
    {
      "vehicleId": "VEH003",
      "capacity": 120,
      "currentLatitude": 28.4950,
      "currentLongitude": 77.0895,
      "currentAddress": "Cyber Hub, Gurgaon"
    },
    {
      "vehicleId": "VEH004",
      "capacity": 90,
      "currentLatitude": 28.6315,
      "currentLongitude": 77.2167,
      "currentAddress": "Connaught Place, Delhi"
    },
    {
      "vehicleId": "VEH005",
      "capacity": 110,
      "currentLatitude": 28.6517,
      "currentLongitude": 77.1906,
      "currentAddress": "Karol Bagh, Delhi"
    }
  ]
}
"@

try {
    $vResp = Invoke-RestMethod -Uri "$baseUrl/vehicles" -Method Post -Body $vehiclesJson -ContentType "application/json"
    Write-Host "Vehicles accepted ($($vResp.status))" -ForegroundColor Green
} catch {
    Write-Host "Failed to submit vehicles: $_" -ForegroundColor Red
    exit 1
}

# Ingest orders
Write-Host "Submitting 30 delivery orders..." -ForegroundColor Gray
$ordersJson = @"
{
  "orders": [
    {"orderId": "ORD001", "latitude": 28.6510, "longitude": 77.1900, "address": "Karol Bagh Market, Delhi", "packageWeight": 25, "priority": "HIGH"},
    {"orderId": "ORD002", "latitude": 28.6320, "longitude": 77.2180, "address": "CP Inner Circle, Delhi", "packageWeight": 30, "priority": "HIGH"},
    {"orderId": "ORD003", "latitude": 28.6550, "longitude": 77.1880, "address": "Dev Nagar, Karol Bagh, Delhi", "packageWeight": 15, "priority": "MEDIUM"},
    {"orderId": "ORD004", "latitude": 28.6300, "longitude": 77.2200, "address": "Barakhamba Road, Delhi", "packageWeight": 20, "priority": "LOW"},
    {"orderId": "ORD005", "latitude": 28.6480, "longitude": 77.1920, "address": "Pusa Road, Delhi", "packageWeight": 35, "priority": "HIGH"},
    {"orderId": "ORD006", "latitude": 28.6340, "longitude": 77.2150, "address": "Janpath, Delhi", "packageWeight": 10, "priority": "MEDIUM"},
    {"orderId": "ORD007", "latitude": 28.6530, "longitude": 77.1850, "address": "Rajendra Place, Delhi", "packageWeight": 40, "priority": "LOW"},
    {"orderId": "ORD008", "latitude": 28.6280, "longitude": 77.2140, "address": "Tolstoy Marg, Delhi", "packageWeight": 25, "priority": "HIGH"},
    {"orderId": "ORD009", "latitude": 28.6490, "longitude": 77.1890, "address": "Karol Bagh Metro, Delhi", "packageWeight": 15, "priority": "MEDIUM"},
    {"orderId": "ORD010", "latitude": 28.6360, "longitude": 77.2170, "address": "Kasturba Gandhi Marg, Delhi", "packageWeight": 30, "priority": "LOW"},
    {"orderId": "ORD011", "latitude": 28.5700, "longitude": 77.3250, "address": "Atta Market, Sector 18, Noida", "packageWeight": 20, "priority": "HIGH"},
    {"orderId": "ORD012", "latitude": 28.5680, "longitude": 77.3280, "address": "Gautam Buddha Park, Noida", "packageWeight": 35, "priority": "HIGH"},
    {"orderId": "ORD013", "latitude": 28.5720, "longitude": 77.3240, "address": "Wave Mall, Sector 18, Noida", "packageWeight": 15, "priority": "MEDIUM"},
    {"orderId": "ORD014", "latitude": 28.5740, "longitude": 77.3200, "address": "Sector 16 Metro, Noida", "packageWeight": 25, "priority": "LOW"},
    {"orderId": "ORD015", "latitude": 28.5660, "longitude": 77.3300, "address": "Great India Place, Noida", "packageWeight": 30, "priority": "HIGH"},
    {"orderId": "ORD016", "latitude": 28.5750, "longitude": 77.3220, "address": "Film City, Sector 16A, Noida", "packageWeight": 10, "priority": "MEDIUM"},
    {"orderId": "ORD017", "latitude": 28.5640, "longitude": 77.3320, "address": "Sector 38A, Noida", "packageWeight": 40, "priority": "LOW"},
    {"orderId": "ORD018", "latitude": 28.5710, "longitude": 77.3270, "address": "DLF Mall of India, Noida", "packageWeight": 20, "priority": "HIGH"},
    {"orderId": "ORD019", "latitude": 28.5690, "longitude": 77.3230, "address": "Sector 18 Commercial, Noida", "packageWeight": 15, "priority": "MEDIUM"},
    {"orderId": "ORD020", "latitude": 28.5760, "longitude": 77.3210, "address": "Sector 15, Noida", "packageWeight": 25, "priority": "LOW"},
    {"orderId": "ORD021", "latitude": 28.4940, "longitude": 77.0890, "address": "Cyber City Building 10, Gurgaon", "packageWeight": 30, "priority": "HIGH"},
    {"orderId": "ORD022", "latitude": 28.4960, "longitude": 77.0910, "address": "DLF Phase 2, Gurgaon", "packageWeight": 25, "priority": "HIGH"},
    {"orderId": "ORD023", "latitude": 28.4930, "longitude": 77.0870, "address": "Cyber Hub Amphitheatre, Gurgaon", "packageWeight": 15, "priority": "MEDIUM"},
    {"orderId": "ORD024", "latitude": 28.4910, "longitude": 77.0850, "address": "Belvedere Towers, Gurgaon", "packageWeight": 35, "priority": "LOW"},
    {"orderId": "ORD025", "latitude": 28.4970, "longitude": 77.0920, "address": "DLF Phase 3, Gurgaon", "packageWeight": 20, "priority": "HIGH"},
    {"orderId": "ORD026", "latitude": 28.4920, "longitude": 77.0880, "address": "Infinity Tower, Gurgaon", "packageWeight": 10, "priority": "MEDIUM"},
    {"orderId": "ORD027", "latitude": 28.4890, "longitude": 77.0830, "address": "Sikanderpur, Gurgaon", "packageWeight": 40, "priority": "LOW"},
    {"orderId": "ORD028", "latitude": 28.4980, "longitude": 77.0940, "address": "MG Road Metro, Gurgaon", "packageWeight": 25, "priority": "HIGH"},
    {"orderId": "ORD029", "latitude": 28.4955, "longitude": 77.0900, "address": "Building 8, Cyber City, Gurgaon", "packageWeight": 15, "priority": "MEDIUM"},
    {"orderId": "ORD030", "latitude": 28.4880, "longitude": 77.0820, "address": "DLF City Court, Gurgaon", "packageWeight": 30, "priority": "LOW"}
  ]
}
"@

try {
    $oResp = Invoke-RestMethod -Uri "$baseUrl/orders" -Method Post -Body $ordersJson -ContentType "application/json"
    Write-Host "Orders accepted ($($oResp.status))" -ForegroundColor Green
} catch {
    Write-Host "Failed to submit orders: $_" -ForegroundColor Red
    exit 1
}

# Fetch plan
try {
    $plan = Invoke-RestMethod -Uri "$baseUrl/plan" -Method Get
    Write-Host "`n--- Dispatch Plan ---" -ForegroundColor Cyan
    Write-Host "Vehicles: $($plan.dispatchPlan.Count) | Unassigned orders: $($plan.unassignedOrders.Count)" -ForegroundColor Gray
    
    foreach ($entry in $plan.dispatchPlan) {
        Write-Host "Vehicle $($entry.vehicleId): load = $($entry.totalLoad) kg, route = $($entry.totalDistance), stops = $($entry.assignedOrders.Count)" -ForegroundColor White
    }
    
    Write-Host "`nPlan URL: $baseUrl/plan`n" -ForegroundColor Gray
} catch {
    Write-Host "Failed to fetch plan: $_" -ForegroundColor Red
}
