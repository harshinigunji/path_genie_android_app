<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$stream_id = intval($_GET['stream_id'] ?? 0);

if (!$stream_id) {
    error("stream_id is required");
}

/*
  Fetch entrance exams related to a stream
*/
$res = mysqli_query($conn, "
    SELECT 
        e.exam_id,
        e.exam_name,
        e.conducting_body,
        e.exam_stage,
        e.overview,
        e.eligibility
    FROM stream_exams se
    INNER JOIN entrance_exams e
        ON se.exam_id = e.exam_id
    WHERE se.stream_id = $stream_id
");

if (!$res) {
    error(mysqli_error($conn));
}

$exams = [];

while ($row = mysqli_fetch_assoc($res)) {
    $exams[] = $row;
}

success($exams);
