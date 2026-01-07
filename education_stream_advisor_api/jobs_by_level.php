<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

/*
  This API returns all jobs available for a given education level.
  It fetches jobs through the streams that are associated with the education level.
  
  For example:
  - Education Level 2 (12th Science) -> Stream 7 (Engineering B.Tech) -> Jobs 1,2,3,4...
*/

$education_level_id = intval($_GET['education_level_id'] ?? 0);

if (!$education_level_id) {
    error("education_level_id is required");
}

/* ---------- FETCH JOBS FOR LEVEL ---------- */
$res = mysqli_query($conn, "
    SELECT DISTINCT
        j.job_id,
        j.job_name,
        j.job_type,
        j.description,
        j.required_education,
        j.average_salary
    FROM streams s
    INNER JOIN stream_jobs sj ON s.stream_id = sj.stream_id
    INNER JOIN jobs j ON sj.job_id = j.job_id
    WHERE s.education_level_id = $education_level_id
    ORDER BY j.job_type ASC, j.job_name ASC
");

if (!$res) {
    error(mysqli_error($conn));
}

$jobs = [];

while ($row = mysqli_fetch_assoc($res)) {
    $jobs[] = $row;
}

/* ---------- RESPONSE ---------- */
success($jobs);
