<?php
/**
 * get_user_roadmap.php - Get user roadmap with steps
 * Compatible with different table schemas (id vs roadmap_id)
 */
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

include("config/db.php");

$roadmap_id = intval($_GET['roadmap_id'] ?? 0);

error_log("get_user_roadmap.php called with roadmap_id: " . $roadmap_id);

if ($roadmap_id <= 0) {
    echo json_encode([
        "status" => false,
        "message" => "roadmap_id is required"
    ]);
    exit;
}

// Check which column is the primary key (id or roadmap_id)
$columnsRes = mysqli_query($conn, "DESCRIBE user_roadmaps");
$idColumn = 'id';  // Default
$hasRoadmapIdColumn = false;

if ($columnsRes) {
    while ($col = mysqli_fetch_assoc($columnsRes)) {
        if ($col['Field'] == 'roadmap_id') {
            $hasRoadmapIdColumn = true;
            $idColumn = 'roadmap_id';
            break;
        }
    }
}

error_log("Using id column: " . $idColumn);

// Get roadmap details
$roadmap_result = mysqli_query($conn, "
    SELECT 
        $idColumn as roadmap_id,
        user_id,
        title,
        target_job_name,
        target_salary,
        created_at
    FROM user_roadmaps
    WHERE $idColumn = $roadmap_id
    LIMIT 1
");

if (!$roadmap_result) {
    error_log("Query error: " . mysqli_error($conn));
    echo json_encode([
        "status" => false,
        "message" => "Database error: " . mysqli_error($conn)
    ]);
    exit;
}

if (mysqli_num_rows($roadmap_result) == 0) {
    echo json_encode([
        "status" => false,
        "message" => "Roadmap not found"
    ]);
    exit;
}

$roadmap = mysqli_fetch_assoc($roadmap_result);

// Get all steps
$steps_result = mysqli_query($conn, "
    SELECT 
        step_order,
        step_type,
        reference_id,
        title,
        description,
        icon
    FROM user_roadmap_steps
    WHERE roadmap_id = $roadmap_id
    ORDER BY step_order ASC
");

$steps = [];
if ($steps_result) {
    while ($row = mysqli_fetch_assoc($steps_result)) {
        $steps[] = [
            "step_order" => intval($row['step_order']),
            "step_type" => $row['step_type'],
            "reference_id" => intval($row['reference_id'] ?? 0),
            "title" => $row['title'],
            "description" => $row['description'],
            "icon" => $row['icon']
        ];
    }
}

error_log("Found " . count($steps) . " steps for roadmap " . $roadmap_id);

$roadmap['steps'] = $steps;

echo json_encode([
    "status" => true,
    "data" => $roadmap
]);
?>
