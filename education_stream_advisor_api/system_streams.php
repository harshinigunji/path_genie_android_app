<?php
/**
 * system_streams.php - Get streams for System Generate P2
 * Returns streams based on education level ID
 */
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

include("config/db.php");

$education_level_id = intval($_GET['education_level_id'] ?? 0);

// Return all streams if no education_level_id provided
$where_clause = "";
if ($education_level_id > 0) {
    $where_clause = "WHERE education_level_id = $education_level_id";
}

$res = mysqli_query($conn, "
    SELECT 
        stream_id,
        stream_name,
        description,
        difficulty_level,
        duration,
        education_level_id
    FROM streams
    $where_clause
    ORDER BY stream_name ASC
");

if (!$res) {
    echo json_encode([
        "success" => false,
        "message" => "Database error: " . mysqli_error($conn)
    ]);
    exit;
}

$streams = [];

while ($row = mysqli_fetch_assoc($res)) {
    $streams[] = [
        "stream_id" => intval($row['stream_id']),
        "stream_name" => $row['stream_name'],
        "description" => $row['description'] ?? "",
        "difficulty_level" => $row['difficulty_level'] ?? "",
        "duration" => $row['duration'] ?? ""
    ];
}

echo json_encode([
    "success" => true,
    "count" => count($streams),
    "data" => $streams
]);
?>
