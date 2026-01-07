<?php
/**
 * get_user_roadmap_steps.php - Get steps for a user-created roadmap
 * Returns steps from user_roadmap_steps table
 * Compatible with different table schemas
 */
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

include("config/db.php");

$roadmap_id = intval($_GET['roadmap_id'] ?? 0);

error_log("get_user_roadmap_steps.php called with roadmap_id: " . $roadmap_id);

if ($roadmap_id <= 0) {
    echo json_encode([
        "status" => false,
        "success" => false,
        "message" => "roadmap_id is required"
    ]);
    exit;
}

// Check which column is the primary key (id or roadmap_id)
$columnsRes = mysqli_query($conn, "DESCRIBE user_roadmaps");
$idColumn = 'id';  // Default
while ($col = mysqli_fetch_assoc($columnsRes)) {
    if ($col['Field'] == 'roadmap_id' && $col['Key'] == 'PRI') {
        $idColumn = 'roadmap_id';
        break;
    }
}

// Get roadmap info
$roadmapRes = mysqli_query($conn, "
    SELECT $idColumn as roadmap_id, title, target_job_name, target_salary
    FROM user_roadmaps
    WHERE $idColumn = $roadmap_id
");

if (!$roadmapRes || mysqli_num_rows($roadmapRes) == 0) {
    echo json_encode([
        "status" => false,
        "success" => false,
        "message" => "Roadmap not found"
    ]);
    exit;
}

$roadmap = mysqli_fetch_assoc($roadmapRes);

// Get steps
$stepsRes = mysqli_query($conn, "
    SELECT 
        step_order,
        step_type,
        title,
        description,
        icon
    FROM user_roadmap_steps
    WHERE roadmap_id = $roadmap_id
    ORDER BY step_order ASC
");

if (!$stepsRes) {
    error_log("Error fetching steps: " . mysqli_error($conn));
}

$steps = [];
if ($stepsRes) {
    while ($row = mysqli_fetch_assoc($stepsRes)) {
        $steps[] = [
            "step_order" => intval($row['step_order']),
            "step_type" => $row['step_type'],
            "title" => $row['title'],
            "description" => $row['description'],
            "icon" => $row['icon']
        ];
    }
}

error_log("Found " . count($steps) . " steps for roadmap " . $roadmap_id);

echo json_encode([
    "status" => true,
    "success" => true,
    "data" => $steps,
    "roadmap" => $roadmap
]);
?>
