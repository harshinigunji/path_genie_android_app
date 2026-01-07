<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$exam_id = intval($_GET['exam_id'] ?? 0);

if (!$exam_id) {
    error("exam_id is required");
}

/* ---------- FETCH EXAM DETAILS ---------- */
$res = mysqli_query($conn, "
    SELECT 
        exam_id,
        exam_name,
        conducting_body,
        exam_stage,
        overview,
        eligibility,
        exam_pattern,
        application_period,
        outcome
    FROM entrance_exams
    WHERE exam_id = $exam_id
    LIMIT 1
");

if (!$res || mysqli_num_rows($res) === 0) {
    error("Exam not found");
}

$exam = mysqli_fetch_assoc($res);

success($exam);
