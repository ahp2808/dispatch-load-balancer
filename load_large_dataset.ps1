# Dispatch Load Balancer - Large Dataset Scalability Benchmark
#
# Usage:
#   .\load_large_dataset.ps1
#   .\load_large_dataset.ps1 -VehicleCount 60 -OrderCount 500

param(
    [int]$VehicleCount = 50,
    [int]$OrderCount = 300,
    [string]$BaseUrl = "http://localhost:8080/api/dispatch"
)

Write-Host "`n--- Dispatch Load Balancer: Scalability Benchmark ---" -ForegroundColor Cyan
Write-Host "Target: $BaseUrl | Vehicles: $VehicleCount | Orders: $OrderCount`n" -ForegroundColor Gray

# Reset previous data
$healthWatch = [System.Diagnostics.Stopwatch]::StartNew()
try {
    Invoke-RestMethod -Uri "$BaseUrl/orders" -Method Delete -ErrorAction Stop | Out-Null
    Invoke-RestMethod -Uri "$BaseUrl/vehicles" -Method Delete -ErrorAction Stop | Out-Null
    $healthWatch.Stop()
    Write-Host "Cleared previous state ($($healthWatch.ElapsedMilliseconds) ms)" -ForegroundColor Gray
} catch {
    Write-Host "Could not connect to dispatch server at $BaseUrl" -ForegroundColor Red
    Write-Host "Make sure the application is running: .\mvnw.cmd spring-boot:run" -ForegroundColor Gray
    exit 1
}

# Regional Hubs
$hubs = @(
    @{
        Code = "DEL"
        Name = "Delhi NCR Hub"
        BaseLat = 28.6139
        BaseLon = 77.2090
        DepotAddress = "Okhla Industrial Area Phase III, New Delhi"
        SubLocalities = @("Connaught Place", "Karol Bagh", "Nehru Place", "Noida Sector 62", "Gurgaon Cyber City", "Faridabad Industrial Area", "Ghaziabad Hub", "Okhla Phase II")
    },
    @{
        Code = "BOM"
        Name = "Mumbai Metropolitan Hub"
        BaseLat = 19.0760
        BaseLon = 72.8777
        DepotAddress = "Bhiwandi Logistics Hub, Mumbai, Maharashtra"
        SubLocalities = @("Bandra Kurla Complex", "Andheri East MIDC", "Lower Parel", "Powai Tech Park", "Navi Mumbai Vashi", "Thane Wagle Estate", "Bhiwandi Warehouse", "Turbhe MIDC")
    },
    @{
        Code = "BLR"
        Name = "Bengaluru Hub"
        BaseLat = 12.9716
        BaseLon = 77.5946
        DepotAddress = "Peenya Industrial Area 4th Phase, Bengaluru, Karnataka"
        SubLocalities = @("Electronic City Phase 1", "Whitefield ITPL", "Koramangala", "Indiranagar", "Manyata Tech Park", "Bommasandra Industrial", "Peenya Phase 2", "Marathahalli")
    },
    @{
        Code = "HYD"
        Name = "Hyderabad Hub"
        BaseLat = 17.3850
        BaseLon = 78.4867
        DepotAddress = "Sanathnagar Industrial Estate, Hyderabad, Telangana"
        SubLocalities = @("HITEC City", "Gachibowli Financial District", "Madhapur", "Cherlapally IDA", "Jeedimetla Industrial", "KPHB Colony", "Begumpet", "Shamshabad Cargo")
    },
    @{
        Code = "MAA"
        Name = "Chennai Hub"
        BaseLat = 13.0827
        BaseLon = 80.2707
        DepotAddress = "Guindy Industrial Estate, Chennai, Tamil Nadu"
        SubLocalities = @("Ambattur Industrial Area", "Guindy Estate", "Sriperumbudur Auto Hub", "OMR IT Highway", "Tidel Park", "Ennore Logistics", "Manali Belt", "Porur Commercial")
    }
)

# Generate vehicles
$vehiclesList = [System.Collections.Generic.List[PSObject]]::new()
$totalFleetCapacity = 0

for ($i = 1; $i -le $VehicleCount; $i++) {
    $hub = $hubs[($i - 1) % $hubs.Count]
    $hubIndex = [math]::Floor(($i - 1) / $hubs.Count) + 1
    
    $latOffset = [math]::Round(((($i * 17) % 50) - 25) * 0.0015, 4)
    $lonOffset = [math]::Round(((($i * 31) % 50) - 25) * 0.0015, 4)
    
    $capacity = 150 + (($i * 7) % 5) * 10
    $totalFleetCapacity += $capacity
    
    $vId = "vehicle-$($hub.Code.ToLower())-$($hubIndex.ToString('00'))"
    
    $vehicleObj = [PSCustomObject]@{
        vehicleId = $vId
        capacity = $capacity
        currentLatitude = [math]::Round($hub.BaseLat + $latOffset, 4)
        currentLongitude = [math]::Round($hub.BaseLon + $lonOffset, 4)
        currentAddress = "$($hub.DepotAddress) [Bay $hubIndex]"
    }
    $vehiclesList.Add($vehicleObj)
}

# Generate orders
$ordersList = [System.Collections.Generic.List[PSObject]]::new()
$totalOrderWeight = 0
$priorities = @("HIGH", "MEDIUM", "LOW")

for ($j = 1; $j -le $OrderCount; $j++) {
    $hub = $hubs[($j - 1) % $hubs.Count]
    $locality = $hub.SubLocalities[($j - 1) % $hub.SubLocalities.Count]
    
    $latOffset = [math]::Round(((($j * 23) % 100) - 50) * 0.0028, 4)
    $lonOffset = [math]::Round(((($j * 37) % 100) - 50) * 0.0028, 4)
    
    $weight = 10 + (($j * 11) % 26)
    $totalOrderWeight += $weight
    $priority = $priorities[($j - 1) % 3]
    
    $oId = "order-$($j.ToString('0000'))"
    
    $orderObj = [PSCustomObject]@{
        orderId = $oId
        latitude = [math]::Round($hub.BaseLat + $latOffset, 4)
        longitude = [math]::Round($hub.BaseLon + $lonOffset, 4)
        address = "$locality, $($hub.Name)"
        packageWeight = $weight
        priority = $priority
    }
    $ordersList.Add($orderObj)
}

Write-Host "Generated $($vehiclesList.Count) vehicles (capacity: $totalFleetCapacity kg) and $($ordersList.Count) orders (load: $totalOrderWeight kg)" -ForegroundColor Gray

# Submit vehicles
$vehiclesPayload = @{ vehicles = $vehiclesList } | ConvertTo-Json -Depth 5
$vIngestWatch = [System.Diagnostics.Stopwatch]::StartNew()
try {
    $vResp = Invoke-RestMethod -Uri "$BaseUrl/vehicles" -Method Post -Body $vehiclesPayload -ContentType "application/json"
    $vIngestWatch.Stop()
    Write-Host "POST /vehicles: $($vIngestWatch.ElapsedMilliseconds) ms ($($vehiclesList.Count) vehicles)" -ForegroundColor Green
} catch {
    Write-Host "Failed to ingest vehicles: $_" -ForegroundColor Red
    exit 1
}

# Submit orders
$ordersPayload = @{ orders = $ordersList } | ConvertTo-Json -Depth 5
$oIngestWatch = [System.Diagnostics.Stopwatch]::StartNew()
try {
    $oResp = Invoke-RestMethod -Uri "$BaseUrl/orders" -Method Post -Body $ordersPayload -ContentType "application/json"
    $oIngestWatch.Stop()
    Write-Host "POST /orders  : $($oIngestWatch.ElapsedMilliseconds) ms ($($ordersList.Count) orders)" -ForegroundColor Green
} catch {
    Write-Host "Failed to ingest orders: $_" -ForegroundColor Red
    exit 1
}

# Compute plan
$computeWatch = [System.Diagnostics.Stopwatch]::StartNew()
try {
    $plan = Invoke-RestMethod -Uri "$BaseUrl/plan" -Method Get
    $computeWatch.Stop()
    Write-Host "GET  /plan    : $($computeWatch.ElapsedMilliseconds) ms (dispatch plan computed)" -ForegroundColor Green
} catch {
    Write-Host "Dispatch plan computation failed: $_" -ForegroundColor Red
    exit 1
}

# Metrics
$computationMs = $computeWatch.ElapsedMilliseconds
$totalPipelineMs = $vIngestWatch.ElapsedMilliseconds + $oIngestWatch.ElapsedMilliseconds + $computationMs

$assignedOrdersCount = 0
$totalDispatchedLoad = 0.0
$activeVehiclesCount = 0
$idleVehiclesCount = 0
$totalRouteKm = 0.0
$vehicleLoads = [System.Collections.Generic.List[double]]::new()

foreach ($entry in $plan.dispatchPlan) {
    $assignedStops = $entry.assignedOrders.Count
    $load = [double]$entry.totalLoad
    $assignedOrdersCount += $assignedStops
    $totalDispatchedLoad += $load
    
    $distStr = "$($entry.totalDistance)".Replace(" km", "").Trim()
    $distVal = 0.0
    [double]::TryParse($distStr, [ref]$distVal) | Out-Null
    $totalRouteKm += $distVal
    
    if ($assignedStops -gt 0) {
        $activeVehiclesCount++
        $vehicleLoads.Add($load)
    } else {
        $idleVehiclesCount++
    }
}

$unassignedOrdersCount = $plan.unassignedOrders.Count
$throughput = if ($computationMs -gt 0) { [math]::Round(($OrderCount / ($computationMs / 1000.0)), 1) } else { "N/A" }
$timePerOrderUs = if ($OrderCount -gt 0) { [math]::Round(($computationMs * 1000.0) / $OrderCount, 1) } else { 0 }

$fleetUtilization = if ($totalFleetCapacity -gt 0) { [math]::Round(($totalDispatchedLoad / $totalFleetCapacity) * 100, 2) } else { 0 }
$avgLoadActive = if ($activeVehiclesCount -gt 0) { [math]::Round($totalDispatchedLoad / $activeVehiclesCount, 2) } else { 0 }
$minLoad = if ($vehicleLoads.Count -gt 0) { ($vehicleLoads | Measure-Object -Minimum).Minimum } else { 0 }
$maxLoad = if ($vehicleLoads.Count -gt 0) { ($vehicleLoads | Measure-Object -Maximum).Maximum } else { 0 }

Write-Host "`n--- Performance Summary ---" -ForegroundColor Cyan
Write-Host "Ingestion Latency   : Vehicles $($vIngestWatch.ElapsedMilliseconds) ms | Orders $($oIngestWatch.ElapsedMilliseconds) ms"
Write-Host "Algorithm Latency   : $computationMs ms"
Write-Host "Total Round-Trip    : $totalPipelineMs ms"
Write-Host "Throughput          : $throughput orders/sec ($timePerOrderUs us/order)"

Write-Host "`n--- Fleet Summary ---" -ForegroundColor Cyan
Write-Host "Fleet Capacity      : $totalFleetCapacity kg across $($vehiclesList.Count) vehicles"
Write-Host "Dispatched Load     : $totalDispatchedLoad kg ($fleetUtilization % utilization)"
Write-Host "Active Vehicles     : $activeVehiclesCount / $($vehiclesList.Count) (idle: $idleVehiclesCount)"
Write-Host "Load / Vehicle (avg): $avgLoadActive kg (min: $minLoad kg, max: $maxLoad kg)"
Write-Host "Total Route Distance: $([math]::Round($totalRouteKm, 2)) km"

Write-Host "`n--- Order Dispatch Summary ---" -ForegroundColor Cyan
Write-Host "Submitted Orders    : $OrderCount"
Write-Host "Dispatched Orders   : $assignedOrdersCount"
Write-Host "Unassigned Orders   : $unassignedOrdersCount"

if ($unassignedOrdersCount -eq 0) {
    Write-Host "Status: All orders dispatched successfully." -ForegroundColor Green
} else {
    Write-Host "Status: $unassignedOrdersCount orders left unassigned due to capacity limit." -ForegroundColor Yellow
}

# Top 5 vehicles
Write-Host "`n--- Sample Routes (Top 5 Vehicles by stops) ---" -ForegroundColor Cyan
$topVehicles = $plan.dispatchPlan | Sort-Object { [int]$_.assignedOrders.Count } -Descending | Select-Object -First 5

foreach ($v in $topVehicles) {
    Write-Host "$($v.vehicleId) | Load: $($v.totalLoad) kg | Dist: $($v.totalDistance) | Stops: $($v.assignedOrders.Count)" -ForegroundColor White
    $sampleStops = $v.assignedOrders | Select-Object -First 3
    foreach ($s in $sampleStops) {
        Write-Host "   -> [$($s.priority)] $($s.orderId) ($($s.packageWeight) kg) - $($s.address)" -ForegroundColor Gray
    }
    if ($v.assignedOrders.Count -gt 3) {
        Write-Host "   ... and $(($v.assignedOrders.Count) - 3) more stops" -ForegroundColor DarkGray
    }
}

Write-Host "`nPlan URL: $BaseUrl/plan`n" -ForegroundColor Gray
