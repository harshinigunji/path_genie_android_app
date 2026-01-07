<?php
/**
 * system_jobs.php - Get jobs for System Generate P3
 * Returns jobs based on stream ID
 */
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

include("config/db.php");

$stream_id = intval($_GET['stream_id'] ?? 0);

if ($stream_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "stream_id is required"
    ]);
    exit;
}

// Get jobs linked to this stream
$res = mysqli_query($conn, "
    SELECT 
        j.job_id,
        j.job_name,
        j.job_type,
        j.description,
        j.average_salary
    FROM stream_jobs sj
    INNER JOIN jobs j ON sj.job_id = j.job_id
    WHERE sj.stream_id = $stream_id
    ORDER BY j.job_name ASC
");

if (!$res) {
    echo json_encode([
        "success" => false,
        "message" => "Database error: " . mysqli_error($conn)
    ]);
    exit;
}

$jobs = [];

while ($row = mysqli_fetch_assoc($res)) {
    $jobs[] = [
        "job_id" => intval($row['job_id']),
        "job_name" => $row['job_name'],
        "job_type" => $row['job_type'] ?? "",
        "description" => $row['description'] ?? "",
        "average_salary" => $row['average_salary'] ?? ""
    ];
}

echo json_encode([
    "success" => true,
    "count" => count($jobs),
    "data" => $jobs
]);
?>
