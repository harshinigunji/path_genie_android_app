<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$user_id = intval($_GET['user_id'] ?? 0);

if (!$user_id) {
    error("user_id is required");
}

// Get all roadmaps for user
$result = mysqli_query($conn, "
    SELECT 
        roadmap_id,
        title,
        target_job_name,
        target_salary,
        created_at
    FROM user_roadmaps
    WHERE user_id = $user_id
    ORDER BY created_at DESC
");

$roadmaps = [];
while ($row = mysqli_fetch_assoc($result)) {
    $roadmaps[] = $row;
}

success($roadmaps);
?>
