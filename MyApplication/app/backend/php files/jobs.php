<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$stream_id = intval($_GET['stream_id'] ?? 0);

if (!$stream_id) {
    error("stream_id is required");
}

/*
  Fetch jobs related to a stream
*/
$res = mysqli_query($conn, "
    SELECT 
        j.job_id,
        j.job_name,
        j.job_type,
        j.description,
        j.average_salary
    FROM stream_jobs sj
    INNER JOIN jobs j
        ON sj.job_id = j.job_id
    WHERE sj.stream_id = $stream_id
    ORDER BY j.job_name ASC
");

if (!$res) {
    error(mysqli_error($conn));
}

$jobs = [];

while ($row = mysqli_fetch_assoc($res)) {
    $jobs[] = $row;
}

success($jobs);
