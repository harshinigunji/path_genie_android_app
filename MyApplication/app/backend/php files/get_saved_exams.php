<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$user_id = intval($_GET['user_id'] ?? 0);

if (!$user_id) {
    error("user_id is required");
}

/*
  Fetch saved exams with stream context
*/
$res = mysqli_query($conn, "
    SELECT 
        se.id AS saved_exam_id,
        se.saved_at,
        
        e.exam_id,
        e.exam_name,
        e.exam_stage,
        e.conducting_body,
        e.overview,
        
        s.stream_id,
        s.stream_name
    FROM saved_exams se
    INNER JOIN entrance_exams e 
        ON se.exam_id = e.exam_id
    INNER JOIN streams s
        ON se.stream_id = s.stream_id
    WHERE se.user_id = $user_id
    ORDER BY se.saved_at DESC
");

if (!$res) {
    error(mysqli_error($conn));
}

$saved_exams = [];

while ($row = mysqli_fetch_assoc($res)) {
    $saved_exams[] = $row;
}

success($saved_exams);
