<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

/*
  Expected JSON:
  {
    "user_id": 1,
    "exam_id": 4,
    "stream_id": 7
  }
*/

$data = json_decode(file_get_contents("php://input"), true);

$user_id   = intval($data['user_id'] ?? 0);
$exam_id   = intval($data['exam_id'] ?? 0);
$stream_id = intval($data['stream_id'] ?? 0);

if (!$user_id || !$exam_id || !$stream_id) {
    error("user_id, exam_id and stream_id are required");
}

/* ---------- PREVENT DUPLICATES ---------- */
$check = mysqli_query($conn, "
    SELECT id FROM saved_exams
    WHERE user_id = $user_id
      AND exam_id = $exam_id
      AND stream_id = $stream_id
");

if (mysqli_num_rows($check) > 0) {
    success(["message" => "Exam already added to roadmap"]);
}

/* ---------- SAVE EXAM ---------- */
$res = mysqli_query($conn, "
    INSERT INTO saved_exams (user_id, exam_id, stream_id)
    VALUES ($user_id, $exam_id, $stream_id)
");

if (!$res) {
    error("Failed to add exam to roadmap");
}

success([
    "message" => "Exam added to roadmap successfully"
]);
