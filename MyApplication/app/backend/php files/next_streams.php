<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$stream_id = intval($_GET['stream_id'] ?? 0);

if (!$stream_id) {
    error("stream_id is required");
}

/*
  Fetch next possible streams using stream_progression
  current_stream_id  -> next_stream_id
  Also returns education_level_id so we know what level exams to show
*/
$res = mysqli_query($conn, "
    SELECT 
        s.stream_id,
        s.stream_name,
        s.description,
        s.duration,
        s.difficulty_level,
        s.education_level_id
    FROM stream_progression sp
    INNER JOIN streams s 
        ON sp.next_stream_id = s.stream_id
    WHERE sp.current_stream_id = $stream_id
");

if (!$res) {
    error(mysqli_error($conn));
}

$next_streams = [];

while ($row = mysqli_fetch_assoc($res)) {
    $next_streams[] = $row;
}

success($next_streams);
