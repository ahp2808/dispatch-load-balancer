# Multi-State Dispatch Test Script (Maharashtra & Delhi NCR)
# Validates:
# 1. Combined vehicle capacity (680 kg) > combined order weight (335 kg) -> 0 unassigned orders
# 2. Geographic locality: Vehicles in Maharashtra serve Maharashtra orders; Vehicles in Delhi serve Delhi orders.

$baseUrl = "http://localhost:8080/api/dispatch"

Write-Host "`n--- Multi-State Dispatch Test (MH & DL) ---" -ForegroundColor Cyan

# Reset data
try {
    Invoke-RestMethod -Uri "$baseUrl/orders" -Method Delete | Out-Null
    Invoke-RestMethod -Uri "$baseUrl/vehicles" -Method Delete | Out-Null
    Write-Host "Cleared previous state" -ForegroundColor Gray
} catch {
    Write-Host "Notice: Could not reset data: $_" -ForegroundColor Yellow
}

# Ingest vehicles (3 in Maharashtra, 3 in Delhi NCR)
Write-Host "Submitting 6 fleet vehicles across MH and DL..." -ForegroundColor Gray

$vehiclesJson = @"
{
  "vehicles": [
    {
      "vehicleId": "VEH-MH-01",
      "capacity": 120,
      "currentLatitude": 19.0657,
      "currentLongitude": 72.8687,
      "currentAddress": "Bandra Kurla Complex, Mumbai, Maharashtra"
    },
    {
      "vehicleId": "VEH-MH-02",
      "capacity": 100,
      "currentLatitude": 19.1136,
      "currentLongitude": 72.8697,
      "currentAddress": "Andheri East, Mumbai, Maharashtra"
    },
    {
      "vehicleId": "VEH-MH-03",
      "capacity": 110,
      "currentLatitude": 18.5314,
      "currentLongitude": 73.8446,
      "currentAddress": "Shivaji Nagar, Pune, Maharashtra"
    },
    {
      "vehicleId": "VEH-DL-01",
      "capacity": 130,
      "currentLatitude": 28.6315,
      "currentLongitude": 77.2167,
      "currentAddress": "Connaught Place, Delhi"
    },
    {
      "vehicleId": "VEH-DL-02",
      "capacity": 100,
      "currentLatitude": 28.5708,
      "currentLongitude": 77.3260,
      "currentAddress": "Sector 18, Noida, Uttar Pradesh"
    },
    {
      "vehicleId": "VEH-DL-03",
      "capacity": 120,
      "currentLatitude": 28.4950,
      "currentLongitude": 77.0895,
      "currentAddress": "Cyber Hub, Gurgaon, Haryana"
    }
  ]
}
"@

try {
    $vResp = Invoke-RestMethod -Uri "$baseUrl/vehicles" -Method Post -Body $vehiclesJson -ContentType "application/json"
    Write-Host " Vehicles Status: $($vResp.status) - $($vResp.message)" -ForegroundColor Green
} catch {
    Write-Host " Failed to submit vehicles: $_" -ForegroundColor Red
    exit 1
}

# 2. Ingest 16 Delivery Orders (8 in Maharashtra, 8 in Delhi NCR)
# Total Weight: 160 kg (MH) + 175 kg (DL) = 335 kg (< 680 kg capacity)
Write-Host "`n[Step 2] Submitting 16 Delivery Orders across Maharashtra & Delhi NCR..." -ForegroundColor Yellow

$ordersJson = @"
{
  "orders": [
    {"orderId": "ORD-MH-001", "latitude": 19.0596, "longitude": 72.8295, "address": "Bandra West, Mumbai, Maharashtra", "packageWeight": 20, "priority": "HIGH"},
    {"orderId": "ORD-MH-002", "latitude": 19.0016, "longitude": 72.8290, "address": "Lower Parel, Mumbai, Maharashtra", "packageWeight": 25, "priority": "HIGH"},
    {"orderId": "ORD-MH-003", "latitude": 19.1176, "longitude": 72.9060, "address": "Powai IT Park, Mumbai, Maharashtra", "packageWeight": 15, "priority": "MEDIUM"},
    {"orderId": "ORD-MH-004", "latitude": 19.0178, "longitude": 72.8478, "address": "Dadar West, Mumbai, Maharashtra", "packageWeight": 20, "priority": "LOW"},
    {"orderId": "ORD-MH-005", "latitude": 18.5913, "longitude": 73.7389, "address": "Hinjewadi Infotech Park, Pune, Maharashtra", "packageWeight": 25, "priority": "HIGH"},
    {"orderId": "ORD-MH-006", "latitude": 18.5679, "longitude": 73.9143, "address": "Viman Nagar, Pune, Maharashtra", "packageWeight": 20, "priority": "MEDIUM"},
    {"orderId": "ORD-MH-007", "latitude": 18.5074, "longitude": 73.8077, "address": "Kothrud, Pune, Maharashtra", "packageWeight": 15, "priority": "LOW"},
    {"orderId": "ORD-MH-008", "latitude": 18.5158, "longitude": 73.9272, "address": "Magarpatta City, Pune, Maharashtra", "packageWeight": 20, "priority": "MEDIUM"},

    {"orderId": "ORD-DL-001", "latitude": 28.6510, "longitude": 77.1900, "address": "Karol Bagh Market, Delhi", "packageWeight": 25, "priority": "HIGH"},
    {"orderId": "ORD-DL-002", "latitude": 28.6506, "longitude": 77.2303, "address": "Chandni Chowk, Delhi", "packageWeight": 30, "priority": "HIGH"},
    {"orderId": "ORD-DL-003", "latitude": 28.5700, "longitude": 77.2400, "address": "Lajpat Nagar Central Market, Delhi", "packageWeight": 20, "priority": "MEDIUM"},
    {"orderId": "ORD-DL-004", "latitude": 28.5494, "longitude": 77.2001, "address": "Hauz Khas Village, Delhi", "packageWeight": 15, "priority": "LOW"},
    {"orderId": "ORD-DL-005", "latitude": 28.5700, "longitude": 77.3250, "address": "Atta Market, Sector 18, Noida", "packageWeight": 25, "priority": "HIGH"},
    {"orderId": "ORD-DL-006", "latitude": 28.6271, "longitude": 77.3725, "address": "Sector 62 Electronic City, Noida", "packageWeight": 20, "priority": "MEDIUM"},
    {"orderId": "ORD-DL-007", "latitude": 28.4960, "longitude": 77.0910, "address": "DLF Phase 2, Gurgaon", "packageWeight": 20, "priority": "HIGH"},
    {"orderId": "ORD-DL-008", "latitude": 28.4595, "longitude": 77.0988, "address": "Golf Course Road, Gurgaon", "packageWeight": 20, "priority": "LOW"}
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

# Fetch and verify dispatch plan
try {
    $plan = Invoke-RestMethod -Uri "$baseUrl/plan" -Method Get
    Write-Host "`n--- Dispatch Plan Summary ---" -ForegroundColor Cyan

    $allMhCorrect = $true
    $allDlCorrect = $true

    foreach ($entry in $plan.dispatchPlan) {
        $vId = $entry.vehicleId
        $load = $entry.totalLoad
        $dist = $entry.totalDistance
        $stops = $entry.assignedOrders.Count

        Write-Host "`nVehicle $vId | Load: $load kg | Route: $dist | Stops: $stops" -ForegroundColor White

        if ($stops -gt 0) {
            foreach ($o in $entry.assignedOrders) {
                Write-Host "   -> [$($o.priority)] $($o.orderId) ($($o.packageWeight) kg) - $($o.address)" -ForegroundColor Gray

                if ($vId.StartsWith("VEH-MH") -and -not ($o.address -match "Maharashtra|Mumbai|Pune")) {
                    $allMhCorrect = $false
                    Write-Host "      [WARNING] Maharashtra vehicle assigned non-MH order: $($o.address)" -ForegroundColor Red
                }
                if ($vId.StartsWith("VEH-DL") -and -not ($o.address -match "Delhi|Noida|Gurgaon")) {
                    $allDlCorrect = $false
                    Write-Host "      [WARNING] Delhi vehicle assigned non-Delhi order: $($o.address)" -ForegroundColor Red
                }
            }
        } else {
            Write-Host "   (No orders assigned)" -ForegroundColor DarkGray
        }
    }

    Write-Host "`n--- Validation Results ---" -ForegroundColor Cyan

    # Check 1: Zero unassigned orders
    $unassignedCount = $plan.unassignedOrders.Count
    if ($unassignedCount -eq 0) {
        Write-Host "Capacity check: PASSED (all 16 orders assigned, 0 unassigned)" -ForegroundColor Green
    } else {
        Write-Host "Capacity check: FAILED ($unassignedCount unassigned orders)" -ForegroundColor Red
    }

    # Check 2: State/Regional isolation
    if ($allMhCorrect -and $allDlCorrect) {
        Write-Host "State isolation: PASSED (MH vehicles served MH orders, DL vehicles served DL orders)" -ForegroundColor Green
    } else {
        Write-Host "State isolation: FAILED (cross-state order leakage detected)" -ForegroundColor Red
    }

    Write-Host "`nPlan URL: $baseUrl/plan`n" -ForegroundColor Gray
} catch {
    Write-Host "Failed to fetch plan: $_" -ForegroundColor Red
    exit 1
}
