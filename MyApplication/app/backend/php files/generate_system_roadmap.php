<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

/* ---------- HELPERS ---------- */
function esc($conn, $str) {
    return mysqli_real_escape_string($conn, $str);
}

// BFS Pathfinding Function
function findStreamPath($conn, $start_stream_id, $target_job_id) {
    $queue = [];
    $queue[] = [$start_stream_id, [$start_stream_id]]; // [current_stream_id, path_array]
    
    $visited = [];
    $visited[$start_stream_id] = true;

    $iterations = 0;
    $max_iterations = 1000;

    while (count($queue) > 0 && $iterations < $max_iterations) {
        $iterations++;
        [$current_stream_id, $path] = array_shift($queue);

        // Check if job is available at this stream (PRIMARY eligibility)
        $jobRes = mysqli_query($conn, "
            SELECT id FROM stream_jobs 
            WHERE stream_id = $current_stream_id 
              AND job_id = $target_job_id 
              AND eligibility_strength = 'PRIMARY'
        ");

        if ($jobRes && mysqli_num_rows($jobRes) > 0) {
            return $path; // Found the path!
        }

        // Get next possible streams
        $nextRes = mysqli_query($conn, "
            SELECT next_stream_id 
            FROM stream_progression 
            WHERE current_stream_id = $current_stream_id 
              AND progression_type = 'ACADEMIC'
        ");

        while ($row = mysqli_fetch_assoc($nextRes)) {
            $next_id = intval($row['next_stream_id']);
            if (!isset($visited[$next_id])) {
                $visited[$next_id] = true;
                $new_path = $path;
                $new_path[] = $next_id;
                $queue[] = [$next_id, $new_path];
            }
        }
    }

    return null; // No path found
}

/* ---------- INPUT ---------- */
$data = json_decode(file_get_contents("php://input"), true);

$user_id = intval($data['user_id'] ?? 0);
$start_stream_id = intval($data['start_stream_id'] ?? 0);
$target_job_id = intval($data['target_job_id'] ?? 0);

if (!$user_id || !$start_stream_id || !$target_job_id) {
    error("Missing inputs");
}

/* ---------- CREATE ROADMAP ---------- */
mysqli_query($conn, "
    INSERT INTO roadmaps
    (user_id, roadmap_type, start_stream_id, target_job_id)
    VALUES
    ($user_id, 'SYSTEM', $start_stream_id, $target_job_id)
");

$roadmap_id = mysqli_insert_id($conn);

/* ---------- FIND PATH ---------- */
$stream_path = findStreamPath($conn, $start_stream_id, $target_job_id);

if (!$stream_path) {
    // No path found, delete roadmap and return error
    mysqli_query($conn, "DELETE FROM roadmaps WHERE roadmap_id = $roadmap_id");
    error("No academically valid roadmap found for this target job from the selected starting point.");
}

/* ---------- GENERATE STEPS FROM PATH ---------- */
$order = 1;

/* 0️⃣ ADD EDUCATION LEVEL (Starting Point) */
$eduRes = mysqli_query($conn, "
    SELECT el.level_name, el.description
    FROM streams s
    JOIN education_levels el ON s.education_level_id = el.education_level_id
    WHERE s.stream_id = $start_stream_id
");

if ($eduRes && mysqli_num_rows($eduRes) > 0) {
    $edu = mysqli_fetch_assoc($eduRes);
    $level_name = esc($conn, $edu['level_name']);
    $level_desc = esc($conn, $edu['description']);

    mysqli_query($conn, "
        INSERT INTO roadmap_steps
        (roadmap_id, step_order, step_type, title, description, icon)
        VALUES
        ($roadmap_id, $order, 'Education',
         '$level_name',
         'Selected education level',
         'ic_education')
    ");
    $order++;
}

$isFirstStream = true;

foreach ($stream_path as $current_stream_id) {

    /* 1️⃣ ADD ENTRANCE EXAMS (before stream, except for first stream) */
    if (!$isFirstStream) {
        $examRes = mysqli_query($conn, "
            SELECT e.exam_name
            FROM stream_exams se
            JOIN entrance_exams e ON se.exam_id = e.exam_id
            WHERE se.stream_id = $current_stream_id
              AND se.exam_role = 'MANDATORY'
        ");

        $exam_names = [];
        while ($exam = mysqli_fetch_assoc($examRes)) {
            $exam_names[] = $exam['exam_name'];
        }

        if (count($exam_names) > 0) {
            $combined_exams = esc($conn, implode(" or ", $exam_names));
            
            mysqli_query($conn, "
                INSERT INTO roadmap_steps
                (roadmap_id, step_order, step_type, title, description, icon)
                VALUES
                ($roadmap_id, $order, 'EXAM',
                 '$combined_exams',
                 'Entrance examination',
                 'ic_exam')
            ");
            $order++;
        }
    }

    /* 2️⃣ ADD CURRENT STREAM */
    $streamRes = mysqli_query($conn, "
        SELECT stream_name, description
        FROM streams
        WHERE stream_id = $current_stream_id
    ");

    if ($streamRes && mysqli_num_rows($streamRes) > 0) {
        $stream = mysqli_fetch_assoc($streamRes);
        $stream_name = esc($conn, $stream['stream_name']);
        $stream_desc = esc($conn, $stream['description'] ?? 'Education stream');

        mysqli_query($conn, "
            INSERT INTO roadmap_steps
            (roadmap_id, step_order, step_type, title, description, icon)
            VALUES
            ($roadmap_id, $order, 'STREAM',
             '$stream_name',
             '$stream_desc',
             'ic_stream')
        ");
        $order++;
    }

    $isFirstStream = false;
}

/* 3️⃣ ADD TARGET JOB (After the last stream) */
// Verify one last time that the job is indeed there (it should be allowed if path was found)
$jobRes = mysqli_query($conn, "
    SELECT job_name 
    FROM jobs 
    WHERE job_id = $target_job_id
");

if ($jobRes && mysqli_num_rows($jobRes) > 0) {
    $job = mysqli_fetch_assoc($jobRes);
    $job_name = esc($conn, $job['job_name']);

    mysqli_query($conn, "
        INSERT INTO roadmap_steps
        (roadmap_id, step_order, step_type, title, description, icon)
        VALUES
        ($roadmap_id, $order, 'JOB',
         '$job_name',
         'Target career goal',
         'ic_job')
    ");
}

/* ---------- SUCCESS ---------- */
success([
    "roadmap_id" => $roadmap_id,
    "message" => "Academically valid system roadmap generated successfully",
    "path_ids" => $stream_path
]);
