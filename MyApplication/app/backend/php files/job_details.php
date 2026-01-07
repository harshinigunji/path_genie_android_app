<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$job_id = intval($_GET['job_id'] ?? 0);

if (!$job_id) {
    error("job_id is required");
}

/* ---------- FETCH JOB DETAILS ---------- */
$res = mysqli_query($conn, "
    SELECT 
        job_id,
        job_name,
        job_type,
        description,
        required_education,
        required_exams,
        career_growth,
        average_salary
    FROM jobs
    WHERE job_id = $job_id
    LIMIT 1
");

if (!$res || mysqli_num_rows($res) === 0) {
    error("Job not found");
}

$job = mysqli_fetch_assoc($res);

success($job);
