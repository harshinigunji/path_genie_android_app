<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$data = json_decode(file_get_contents("php://input"), true);

$roadmap_id = intval($data['roadmap_id'] ?? 0);
$step_type  = trim($data['step_type'] ?? '');
$title      = trim($data['title'] ?? '');
$description= trim($data['description'] ?? '');
$icon       = trim($data['icon'] ?? '');

if (!$roadmap_id || !$step_type || !$title) {
    error("Missing inputs");
}

/* Get next step order */
$res = mysqli_query($conn, "
    SELECT COALESCE(MAX(step_order), 0) + 1 AS next_order
    FROM roadmap_steps
    WHERE roadmap_id = $roadmap_id
");

$row = mysqli_fetch_assoc($res);
$step_order = intval($row['next_order']);

$title = mysqli_real_escape_string($conn, $title);
$description = mysqli_real_escape_string($conn, $description);
$icon = mysqli_real_escape_string($conn, $icon);

mysqli_query($conn, "
    INSERT INTO roadmap_steps
    (roadmap_id, step_order, step_type, title, description, icon)
    VALUES
    ($roadmap_id, $step_order, '$step_type',
     '$title', '$description', '$icon')
");

/* If JOB, update roadmap table */
if ($step_type === 'JOB') {
    mysqli_query($conn, "
        UPDATE roadmaps
        SET target_job_id = (
            SELECT job_id FROM jobs WHERE job_name = '$title' LIMIT 1
        )
        WHERE roadmap_id = $roadmap_id
    ");
}

success([
    "message" => "Step added successfully",
    "step_order" => $step_order
]);
