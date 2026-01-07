<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$data = json_decode(file_get_contents("php://input"), true);

$step_id = intval($data['step_id'] ?? 0);

if (!$step_id) {
    error("step_id required");
}

mysqli_query($conn, "
    DELETE FROM roadmap_steps
    WHERE step_id = $step_id
");

success([
    "message" => "Roadmap step removed"
]);
