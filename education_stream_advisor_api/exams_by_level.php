<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

/*
  This API returns ALL entrance exams
  valid for a given education level
  (10th / 12th Science / 12th Commerce / 12th Arts / Diploma / UG)
*/

$education_level_id = intval($_GET['education_level_id'] ?? 0);

if (!$education_level_id) {
    error("education_level_id is required");
}

/* ---------- FETCH EXAMS FOR LEVEL ---------- */
$res = mysqli_query($conn, "
    SELECT 
        e.exam_id,
        e.exam_name,
        e.conducting_body,
        e.exam_stage,
        e.overview,
        e.eligibility
    FROM education_level_exams ele
    INNER JOIN entrance_exams e 
        ON ele.exam_id = e.exam_id
    WHERE ele.education_level_id = $education_level_id
    ORDER BY e.exam_name ASC
");

if (!$res) {
    error(mysqli_error($conn));
}

$exams = [];

while ($row = mysqli_fetch_assoc($res)) {
    $exams[] = $row;
}

/* ---------- RESPONSE ---------- */
success($exams);
