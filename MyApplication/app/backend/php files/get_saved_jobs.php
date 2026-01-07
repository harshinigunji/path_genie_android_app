<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$user_id = intval($_GET['user_id'] ?? 0);

if (!$user_id) {
    error("user_id is required");
}

/*
  Fetch saved jobs with stream context
*/
$res = mysqli_query($conn, "
    SELECT 
        sj.id AS saved_job_id,
        sj.saved_at,
        
        j.job_id,
        j.job_name,
        j.job_type,
        j.description,
        j.average_salary,
        
        s.stream_id,
        s.stream_name
    FROM saved_jobs sj
    INNER JOIN jobs j 
        ON sj.job_id = j.job_id
    INNER JOIN streams s
        ON sj.stream_id = s.stream_id
    WHERE sj.user_id = $user_id
    ORDER BY sj.saved_at DESC
");

if (!$res) {
    error(mysqli_error($conn));
}

$saved_jobs = [];

while ($row = mysqli_fetch_assoc($res)) {
    $saved_jobs[] = $row;
}

success($saved_jobs);
