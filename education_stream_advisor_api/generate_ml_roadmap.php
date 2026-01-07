<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

/* ===============================
   INPUT
================================ */


$data = json_decode(file_get_contents("php://input"), true);

$user_id = intval($data['user_id'] ?? 0);
$current_stream_id = intval($data['current_stream_id'] ?? 0);
$target_job_id = intval($data['target_job_id'] ?? 0);

if (!$user_id || !$current_stream_id || !$target_job_id) {
    error("Missing inputs");
}

/* ===============================
   CREATE ROADMAP
================================ */
mysqli_query($conn, "
    INSERT INTO roadmaps (user_id, roadmap_type, start_stream_id, target_job_id)
    VALUES ($user_id, 'SYSTEM_ML', $current_stream_id, $target_job_id)
");

$roadmap_id = mysqli_insert_id($conn);
$step_order = 1;

if (!$roadmap_id) {
    error("Roadmap creation failed");
}

/* ===============================
   SAFETY LIMIT
================================ */
$max_steps = 10;

/* ===============================
   LOOP
================================ */
while ($max_steps-- > 0) {

    /* ---------- FETCH STREAM ---------- */
    $streamRes = mysqli_query($conn, "
        SELECT stream_name, difficulty_level, duration
        FROM streams
        WHERE stream_id = $current_stream_id
    ");

    if (!$streamRes || mysqli_num_rows($streamRes) === 0) {
        break;
    }

    $stream = mysqli_fetch_assoc($streamRes);

    /* ---------- ADD STREAM STEP (ALWAYS) ---------- */
    mysqli_query($conn, "
        INSERT INTO roadmap_steps
        (roadmap_id, step_order, step_type, title, description, icon)
        VALUES
        ($roadmap_id, $step_order++, 'STREAM',
         '{$stream['stream_name']}',
         'Education stream',
         'ic_stream')
    ");

    /* ---------- ADD EXAMS ---------- */
    $examRes = mysqli_query($conn, "
        SELECT e.exam_name
        FROM stream_exams se
        JOIN entrance_exams e ON se.exam_id = e.exam_id
        WHERE se.stream_id = $current_stream_id
    ");

    $exam_count = 0;

    while ($exam = mysqli_fetch_assoc($examRes)) {
        $exam_count++;

        mysqli_query($conn, "
            INSERT INTO roadmap_steps
            (roadmap_id, step_order, step_type, title, description, icon)
            VALUES
            ($roadmap_id, $step_order++, 'EXAM',
             '{$exam['exam_name']}',
             'Entrance examination',
             'ic_exam')
        ");
    }

    /* ---------- CHECK TARGET JOB ---------- */
    $jobRes = mysqli_query($conn, "
        SELECT j.job_name
        FROM stream_jobs sj
        JOIN jobs j ON sj.job_id = j.job_id
        WHERE sj.stream_id = $current_stream_id
          AND sj.job_id = $target_job_id
    ");

    if (mysqli_num_rows($jobRes) > 0) {
        $job = mysqli_fetch_assoc($jobRes);

        mysqli_query($conn, "
            INSERT INTO roadmap_steps
            (roadmap_id, step_order, step_type, title, description, icon)
            VALUES
            ($roadmap_id, $step_order++, 'JOB',
             '{$job['job_name']}',
             'Target career',
             'ic_job')
        ");
        break;
    }

    /* ===============================
       PREPARE ML INPUT (SAFE)
    ================================ */

    /* Normalize difficulty */
    $difficulty_text = strtolower(trim($stream['difficulty_level']));
    if ($difficulty_text === 'easy') $difficulty = 1;
    elseif ($difficulty_text === 'hard') $difficulty = 3;
    else $difficulty = 2; // medium default

    /* Safe duration */
    preg_match('/\d+/', $stream['duration'], $matches);
    $duration = isset($matches[0]) ? intval($matches[0]) : 3;

    /* Alternative paths */
    $altRes = mysqli_query($conn, "
        SELECT COUNT(*) AS cnt
        FROM stream_progression
        WHERE current_stream_id = $current_stream_id
    ");
    $alt = (mysqli_fetch_assoc($altRes)['cnt'] > 1) ? 1 : 0;

    $ml_input = json_encode([
        "current_stream_id" => $current_stream_id,
        "target_job_id" => $target_job_id,
        "stream_difficulty" => $difficulty,
        "stream_duration" => $duration,
        "exam_required" => $exam_count > 0 ? 1 : 0,
        "has_alternative_paths" => $alt
    ]);

    /* ===============================
       CALL ML
    ================================ */
    $pythonPath = "C:\\Users\\harsh\\AppData\\Local\\Programs\\Python\\Python312\\python.exe";
    $scriptPath = __DIR__ . "\\ml\\predict_next_stream.py";

    $process = proc_open(
        "\"$pythonPath\" \"$scriptPath\"",
        [0=>["pipe","r"],1=>["pipe","w"],2=>["pipe","w"]],
        $pipes
    );

    if (!is_resource($process)) {
        break;
    }

    fwrite($pipes[0], $ml_input);
    fclose($pipes[0]);

    $output = stream_get_contents($pipes[1]);
    fclose($pipes[1]);
    fclose($pipes[2]);
    proc_close($process);

    preg_match('/\{.*\}/s', $output, $match);
    if (!isset($match[0])) {
        break;
    }

    $prediction = json_decode($match[0], true);
    if (!isset($prediction['next_stream_id'])) {
        break;
    }

    $current_stream_id = intval($prediction['next_stream_id']);
}

/* ===============================
   DONE
================================ */
success([
    "roadmap_id" => $roadmap_id,
    "message" => "ML-based roadmap generated successfully"
]);
