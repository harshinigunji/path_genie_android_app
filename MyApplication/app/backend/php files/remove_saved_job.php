<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$data = json_decode(file_get_contents("php://input"), true);

$saved_job_id = intval($data['saved_job_id'] ?? 0);

if (!$saved_job_id) {
    error("saved_job_id is required");
}

$res = mysqli_query($conn, "
    DELETE FROM saved_jobs
    WHERE id = $saved_job_id
");

if (!$res) {
    error("Failed to remove saved job");
}

success([
    "message" => "Job removed from saved list"
]);
