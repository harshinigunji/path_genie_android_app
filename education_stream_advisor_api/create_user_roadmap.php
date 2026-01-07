<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$data = json_decode(file_get_contents("php://input"), true);

$user_id = intval($data['user_id'] ?? 0);
$education_level_id = intval($data['education_level_id'] ?? 0);
$start_stream_id = intval($data['start_stream_id'] ?? 0);

if (!$user_id || !$education_level_id || !$start_stream_id) {
    error("Missing inputs");
}

/* Create USER roadmap */
mysqli_query($conn, "
    INSERT INTO roadmaps
    (user_id, roadmap_type, start_education_level_id, start_stream_id)
    VALUES
    ($user_id, 'USER', $education_level_id, $start_stream_id)
");

$roadmap_id = mysqli_insert_id($conn);

/* Add first STREAM step */
$res = mysqli_query($conn, "
    SELECT stream_name
    FROM streams
    WHERE stream_id = $start_stream_id
");

$stream = mysqli_fetch_assoc($res);
$stream_name = mysqli_real_escape_string($conn, $stream['stream_name']);

mysqli_query($conn, "
    INSERT INTO roadmap_steps
    (roadmap_id, step_order, step_type, title, description, icon)
    VALUES
    ($roadmap_id, 1, 'STREAM',
     '$stream_name',
     'Starting stream selected',
     'ic_stream')
");

success([
    "roadmap_id" => $roadmap_id,
    "message" => "User roadmap created"
]);
