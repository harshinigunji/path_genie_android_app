<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

/*
  Expected JSON:
  {
    "user_id": 1,
    "job_id": 2,
    "stream_id": 7
  }
*/

$data = json_decode(file_get_contents("php://input"), true);

$user_id   = intval($data['user_id'] ?? 0);
$job_id    = intval($data['job_id'] ?? 0);
$stream_id = intval($data['stream_id'] ?? 0);

if (!$user_id || !$job_id || !$stream_id) {
    error("user_id, job_id and stream_id are required");
}

/* ---------- PREVENT DUPLICATES ---------- */
$check = mysqli_query($conn, "
    SELECT id FROM saved_jobs
    WHERE user_id = $user_id
      AND job_id = $job_id
      AND stream_id = $stream_id
");

if (mysqli_num_rows($check) > 0) {
    success(["message" => "Job already added to roadmap"]);
}

/* ---------- SAVE JOB ---------- */
$res = mysqli_query($conn, "
    INSERT INTO saved_jobs (user_id, job_id, stream_id)
    VALUES ($user_id, $job_id, $stream_id)
");

if (!$res) {
    error("Failed to add job to roadmap");
}

success([
    "message" => "Job added to roadmap successfully"
]);
