<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$res = mysqli_query($conn, "
    SELECT education_level_id, level_name, description
    FROM education_levels
    ORDER BY education_level_id ASC
");

if (!$res) {
    error("Failed to fetch education levels");
}

$levels = [];

while ($row = mysqli_fetch_assoc($res)) {
    $levels[] = $row;
}

success($levels);
