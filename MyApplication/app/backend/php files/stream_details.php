<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$stream_id = intval($_GET['stream_id'] ?? 0);

if (!$stream_id) {
    error("stream_id is required");
}

$res = mysqli_query($conn, "
    SELECT 
        stream_id,
        stream_name,
        description,
        subjects,
        who_should_choose,
        career_scope,
        duration,
        difficulty_level
    FROM streams
    WHERE stream_id = $stream_id
");

if (!$res) {
    error(mysqli_error($conn));
}

$stream = mysqli_fetch_assoc($res);

if (!$stream) {
    error("Stream not found");
}

success($stream);
