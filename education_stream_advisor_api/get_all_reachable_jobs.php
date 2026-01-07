<?php
/**
 * get_all_reachable_jobs.php - Get ALL jobs reachable from a stream
 * Walks through stream_progression to find all future streams
 * and collects all jobs from those streams
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

// Collect all reachable stream IDs (including current stream)
$reachable_streams = [$stream_id];
$visited = [$stream_id => true];
$queue = [$stream_id];

// BFS to find all reachable streams through progression
while (!empty($queue)) {
    $current = array_shift($queue);
    
    $res = mysqli_query($conn, "
        SELECT next_stream_id 
        FROM stream_progression 
        WHERE current_stream_id = $current
          AND progression_type = 'ACADEMIC'
    ");
    
    while ($row = mysqli_fetch_assoc($res)) {
        $next_id = intval($row['next_stream_id']);
        if (!isset($visited[$next_id])) {
            $visited[$next_id] = true;
            $reachable_streams[] = $next_id;
            $queue[] = $next_id;
        }
    }
}

// Get all unique jobs from all reachable streams
$stream_ids_str = implode(",", $reachable_streams);

$res = mysqli_query($conn, "
    SELECT DISTINCT
        j.job_id,
        j.job_name,
        j.job_type,
        j.description,
        j.average_salary,
        s.stream_name as from_stream
    FROM stream_jobs sj
    INNER JOIN jobs j ON sj.job_id = j.job_id
    INNER JOIN streams s ON sj.stream_id = s.stream_id
    WHERE sj.stream_id IN ($stream_ids_str)
      AND sj.eligibility_strength = 'PRIMARY'
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
$seen_jobs = [];

while ($row = mysqli_fetch_assoc($res)) {
    $job_id = intval($row['job_id']);
    
    // Avoid duplicate jobs (same job from different streams)
    if (!isset($seen_jobs[$job_id])) {
        $seen_jobs[$job_id] = true;
        $jobs[] = [
            "job_id" => $job_id,
            "job_name" => $row['job_name'],
            "job_type" => $row['job_type'] ?? "",
            "description" => $row['description'] ?? "",
            "average_salary" => $row['average_salary'] ?? "",
            "from_stream" => $row['from_stream'] ?? ""
        ];
    }
}

echo json_encode([
    "success" => true,
    "count" => count($jobs),
    "reachable_streams_count" => count($reachable_streams),
    "data" => $jobs
]);
?>
