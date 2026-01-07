<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$education_level_id = intval($_GET['education_level_id'] ?? 0);

if (!$education_level_id) {
    error("education_level_id is required");
}

$res = mysqli_query($conn, "
    SELECT 
        stream_id,
        stream_name,
        description,
        difficulty_level,
        duration
    FROM streams
    WHERE education_level_id = $education_level_id
");

if (!$res) {
    error(mysqli_error($conn));
}

$streams = [];

while ($row = mysqli_fetch_assoc($res)) {
    $streams[] = $row;
}

success($streams);
