<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$roadmap_id = intval($_GET['roadmap_id'] ?? 0);

if (!$roadmap_id) {
    error("roadmap_id is required");
}

$res = mysqli_query($conn, "
    SELECT
        step_id,
        step_order,
        step_type,
        title,
        description,
        icon
    FROM roadmap_steps
    WHERE roadmap_id = $roadmap_id
    ORDER BY step_order ASC
");

if (!$res) {
    error("Failed to fetch roadmap steps");
}

$steps = [];

while ($row = mysqli_fetch_assoc($res)) {
    $steps[] = $row;
}

success([
    "roadmap_id" => $roadmap_id,
    "steps" => $steps
]);
