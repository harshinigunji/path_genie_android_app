<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

require_once("../config/db.php");
require_once("../response.php");

/*
|--------------------------------------------------------------------------
| Read Input
|--------------------------------------------------------------------------
*/
$input = json_decode(file_get_contents("php://input"), true);

$start_stream_id = intval($input['start_stream_id'] ?? 0);
$target_job_id   = intval($input['target_job_id'] ?? 0);

if (!$start_stream_id || !$target_job_id) {
    error("start_stream_id and target_job_id are required");
}

/*
|--------------------------------------------------------------------------
| Helper: Check PRIMARY job eligibility
|--------------------------------------------------------------------------
*/
function hasPrimaryJob($conn, $stream_id, $job_id)
{
    $sql = "
        SELECT 1 FROM stream_jobs
        WHERE stream_id = ?
          AND job_id = ?
          AND eligibility_strength = 'PRIMARY'
        LIMIT 1
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ii", $stream_id, $job_id);
    $stmt->execute();
    $stmt->store_result();
    return $stmt->num_rows > 0;
}

/*
|--------------------------------------------------------------------------
| Helper: Get next streams
|--------------------------------------------------------------------------
*/
function getNextStreams($conn, $stream_id)
{
    $sql = "
        SELECT next_stream_id
        FROM stream_progression
        WHERE current_stream_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $stream_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $next = [];
    while ($row = $result->fetch_assoc()) {
        $next[] = $row['next_stream_id'];
    }
    return $next;
}

/*
|--------------------------------------------------------------------------
| DFS Traversal
|--------------------------------------------------------------------------
*/
$all_paths = [];

function traverse($conn, $current_stream_id, $target_job_id, $current_path, &$all_paths)
{
    $current_path[] = $current_stream_id;

    if (hasPrimaryJob($conn, $current_stream_id, $target_job_id)) {
        $all_paths[] = $current_path;
        return;
    }

    foreach (getNextStreams($conn, $current_stream_id) as $next_stream_id) {
        if (!in_array($next_stream_id, $current_path)) {
            traverse($conn, $next_stream_id, $target_job_id, $current_path, $all_paths);
        }
    }
}

traverse($conn, $start_stream_id, $target_job_id, [], $all_paths);

if (empty($all_paths)) {
    error("No valid roadmap found");
}

/*
|--------------------------------------------------------------------------
| SCORING FUNCTIONS
|--------------------------------------------------------------------------
*/
function scorePathLength($path)
{
    $steps = count($path);
    if ($steps <= 3) return 30;
    if ($steps == 4) return 25;
    if ($steps == 5) return 18;
    return 10;
}

function scoreJobStrength($conn, $stream_id, $job_id)
{
    $sql = "
        SELECT eligibility_strength
        FROM stream_jobs
        WHERE stream_id = ? AND job_id = ?
        LIMIT 1
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ii", $stream_id, $job_id);
    $stmt->execute();
    $row = $stmt->get_result()->fetch_assoc();
    return ($row && $row['eligibility_strength'] === 'PRIMARY') ? 25 : 15;
}

function scoreDetours($path)
{
    $unique = array_unique($path);
    $detours = count($path) - count($unique);
    if ($detours == 0) return 10;
    if ($detours == 1) return 6;
    return 2;
}

/*
|--------------------------------------------------------------------------
| Score All Paths
|--------------------------------------------------------------------------
*/
$scored_paths = [];

foreach ($all_paths as $path) {
    $score  = 0;
    $score += scorePathLength($path);
    $score += scoreJobStrength($conn, end($path), $target_job_id);
    $score += scoreDetours($path);

    $scored_paths[] = [
        "path" => $path,
        "score" => $score
    ];
}

usort($scored_paths, fn($a, $b) => $b['score'] <=> $a['score']);

$best_path = $scored_paths[0]['path'];

/*
|--------------------------------------------------------------------------
| Attach Exams + Build Roadmap
|--------------------------------------------------------------------------
*/
$roadmap = [];
$step = 1;

foreach ($best_path as $stream_id) {

    // Stream & level
    $sql = "
        SELECT s.stream_name, el.education_level_id, el.level_name
        FROM streams s
        JOIN education_levels el ON s.education_level_id = el.education_level_id
        WHERE s.stream_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $stream_id);
    $stmt->execute();
    $stream = $stmt->get_result()->fetch_assoc();

    // Mandatory exams only
    $exam_sql = "
        SELECT e.exam_name
        FROM education_level_exams ele
        JOIN stream_exams se ON ele.exam_id = se.exam_id
        JOIN entrance_exams e ON e.exam_id = se.exam_id
        WHERE ele.education_level_id = ?
          AND se.stream_id = ?
          AND se.exam_role = 'MANDATORY'
    ";
    $exam_stmt = $conn->prepare($exam_sql);
    $exam_stmt->bind_param("ii", $stream['education_level_id'], $stream_id);
    $exam_stmt->execute();
    $exam_res = $exam_stmt->get_result();

    $exams = [];
    while ($row = $exam_res->fetch_assoc()) {
        $exams[] = $row['exam_name'];
    }

    $roadmap[] = [
        "step" => $step++,
        "education_level" => $stream['level_name'],
        "stream" => $stream['stream_name'],
        "exams" => $exams
    ];
}

/*
|--------------------------------------------------------------------------
| Final Job Step
|--------------------------------------------------------------------------
*/
$job_stmt = $conn->prepare("SELECT job_name FROM jobs WHERE job_id = ?");
$job_stmt->bind_param("i", $target_job_id);
$job_stmt->execute();
$job = $job_stmt->get_result()->fetch_assoc();

$roadmap[] = [
    "step" => $step,
    "education_level" => "Career",
    "job" => $job['job_name']
];

success([
    "roadmap" => $roadmap
]);
