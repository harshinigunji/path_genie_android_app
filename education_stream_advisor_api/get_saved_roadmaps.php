<?php
/**
 * get_saved_roadmaps.php - Get ALL saved roadmaps for a user
 * Returns both system-generated and user-created roadmaps
 * Compatible with different table schemas
 */
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

include("config/db.php");

$user_id = intval($_GET['user_id'] ?? 0);

// Debug
error_log("get_saved_roadmaps.php called with user_id: " . $user_id);

if ($user_id <= 0) {
    echo json_encode([
        "success" => false,
        "status" => false,
        "message" => "user_id is required"
    ]);
    exit;
}

$roadmaps = [];

// Get system-generated roadmaps from 'roadmaps' table
$systemRes = mysqli_query($conn, "
    SELECT 
        r.roadmap_id,
        s.stream_name,
        j.job_name,
        j.average_salary,
        r.created_at
    FROM roadmaps r
    LEFT JOIN streams s ON r.start_stream_id = s.stream_id
    LEFT JOIN jobs j ON r.target_job_id = j.job_id
    WHERE r.user_id = $user_id
    ORDER BY r.created_at DESC
");

if ($systemRes) {
    while ($row = mysqli_fetch_assoc($systemRes)) {
        $streamName = $row['stream_name'] ?? 'My Stream';
        $jobName = $row['job_name'] ?? 'My Goal';
        
        $roadmaps[] = [
            "roadmap_id" => intval($row['roadmap_id']),
            "roadmap_type" => "SYSTEM",
            "title" => $streamName . " to " . $jobName,
            "target_job_name" => $jobName,
            "target_salary" => $row['average_salary'] ?? "",
            "from_stream" => $streamName,
            "created_at" => $row['created_at'] ?? ""
        ];
    }
    error_log("Found " . count($roadmaps) . " system roadmaps");
}

// Get user-created roadmaps from 'user_roadmaps' table (if exists)
$tableCheck = mysqli_query($conn, "SHOW TABLES LIKE 'user_roadmaps'");
if (mysqli_num_rows($tableCheck) > 0) {
    
    // Check which column is the primary key (id or roadmap_id)
    $columnsRes = mysqli_query($conn, "DESCRIBE user_roadmaps");
    $hasRoadmapIdColumn = false;
    $hasIdColumn = false;
    
    while ($col = mysqli_fetch_assoc($columnsRes)) {
        if ($col['Field'] == 'roadmap_id') $hasRoadmapIdColumn = true;
        if ($col['Field'] == 'id') $hasIdColumn = true;
    }
    
    // Build query based on actual column names
    $idColumn = $hasRoadmapIdColumn ? 'roadmap_id' : 'id';
    
    $userRes = mysqli_query($conn, "
        SELECT 
            $idColumn as roadmap_id,
            title,
            target_job_name,
            target_salary,
            created_at
        FROM user_roadmaps
        WHERE user_id = $user_id
        ORDER BY created_at DESC
    ");

    if ($userRes) {
        $userCount = 0;
        while ($row = mysqli_fetch_assoc($userRes)) {
            $userCount++;
            $roadmaps[] = [
                "roadmap_id" => intval($row['roadmap_id']),
                "roadmap_type" => "USER",
                "title" => $row['title'] ?? "My Roadmap",
                "target_job_name" => $row['target_job_name'] ?? "",
                "target_salary" => $row['target_salary'] ?? "",
                "from_stream" => "",
                "created_at" => $row['created_at'] ?? ""
            ];
        }
        error_log("Found " . $userCount . " user roadmaps");
    } else {
        error_log("Error fetching user roadmaps: " . mysqli_error($conn));
    }
} else {
    error_log("user_roadmaps table does not exist");
}

// Sort by created_at descending
usort($roadmaps, function($a, $b) {
    $timeA = strtotime($a['created_at'] ?? '1970-01-01');
    $timeB = strtotime($b['created_at'] ?? '1970-01-01');
    return $timeB - $timeA;
});

error_log("Total roadmaps: " . count($roadmaps));

echo json_encode([
    "success" => true,
    "status" => true,
    "count" => count($roadmaps),
    "data" => $roadmaps
]);
?>
