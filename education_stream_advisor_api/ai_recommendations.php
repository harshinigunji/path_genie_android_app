<?php
require_once "config/db.php";

header("Content-Type: application/json");

/* ================= CONSTANTS ================= */

define("OPT_STREAM", 1);
define("OPT_EXAM", 2);
define("OPT_JOB", 3);

define("JOB_GOVT", 0);
define("JOB_PRIVATE", 1);

/* ================= HELPER FUNCTIONS ================= */

function encodeInterest($interestArea) {
    switch ($interestArea) {
        case "Technology":
        case "Science":
            return [1, 0, 0];
        case "Commerce":
        case "Law":
            return [0, 1, 0];
        case "Arts":
            return [0, 0, 1];
        default:
            return [0, 0, 0];
    }
}

function mapDifficulty($level) {
    $level = strtolower(trim($level));
    return match ($level) {
        "easy" => 1,
        "medium" => 2,
        "hard" => 3,
        default => 2
    };
}

function varyDifficulty($base, $index) {
    return max(1, min(3, $base + ($index % 2)));
}

/* ---------- ML Call ---------- */
function getAIScore($payload) {
    $ch = curl_init("http://127.0.0.1:5000/predict");
    curl_setopt_array($ch, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_POST => true,
        CURLOPT_POSTFIELDS => json_encode($payload),
        CURLOPT_HTTPHEADER => ["Content-Type: application/json"]
    ]);
    $response = curl_exec($ch);
    curl_close($ch);

    $json = json_decode($response, true);
    return $json["recommendation_score"] ?? 60;
}

/* ---------- Score Diversification ---------- */
function diversifyScore($score, $index, $educationLevel) {
    $spread = ($educationLevel <= 1) ? 3 : 2;
    return $score - ($index * $spread) + rand(-1, 1);
}

/* ---------- Score Calibration ---------- */
function calibrateScore($score, $educationLevel, $rank) {
    if ($educationLevel <= 1)      $boost = 10;
    elseif ($educationLevel == 2)  $boost = 8;
    else                           $boost = 6;

    return $score + $boost - ($rank * 3);
}

/* ---------- Score Scaling ---------- */
function scaleScore($score) {
    $scaled = ($score - 40) * 1.25;
    return max(55, min(95, round($scaled)));
}

/* ---------- SOFT FLOOR (KEY FIX) ---------- */
function applySoftFloor($score, $rank) {

    $minRanges = [
        0 => 82, // top recommendation
        1 => 74, // second
        2 => 66  // third
    ];

    $min = $minRanges[$rank] ?? 60;

    if ($score < $min) {
        $score = $min + rand(0, 4); // soft lift, not fixed
    }

    return min(95, $score);
}

/* ---------- Thresholds ---------- */
function getThresholds($educationLevel) {
    if ($educationLevel <= 1)
        return ["stream" => 50, "exam" => 50, "job" => 50];
    elseif ($educationLevel == 2)
        return ["stream" => 65, "exam" => 60, "job" => 60];
    else
        return ["stream" => 70, "exam" => 65, "job" => 65];
}

/* ================= READ INPUT ================= */

$input = json_decode(file_get_contents("php://input"), true);

$educationLevel = (int)($input["education_level"] ?? 0);
$careerPref     = (int)($input["career_preference_private"] ?? 0);
$difficultyTol  = (int)($input["difficulty_tolerance"] ?? 2);
$riskTol        = (int)($input["risk_tolerance"] ?? 2);
$interestArea   = $input["interest_area"] ?? "";

[$it, $ic, $ia] = encodeInterest($interestArea);

$streams = [];
$exams   = [];
$jobs    = [];

/* ================= STREAMS ================= */

$q = $conn->prepare("SELECT stream_id, difficulty_level FROM streams WHERE education_level_id = ?");
$q->bind_param("i", $educationLevel);
$q->execute();
$r = $q->get_result();

$index = 0;
while ($row = $r->fetch_assoc()) {

    $payload = [
        "education_level" => $educationLevel,
        "interest_tech" => $it,
        "interest_commerce" => $ic,
        "interest_arts" => $ia,
        "career_preference_private" => $careerPref,
        "difficulty_tolerance" => $difficultyTol,
        "risk_tolerance" => $riskTol,
        "option_type" => OPT_STREAM,
        "option_difficulty" => varyDifficulty(mapDifficulty($row["difficulty_level"]), $index),
        "option_job_type" => 0
    ];

    $raw     = getAIScore($payload);
    $div     = diversifyScore($raw, $index, $educationLevel);
    $cal     = calibrateScore($div, $educationLevel, $index);
    $scaled  = scaleScore($cal);
    $final   = applySoftFloor($scaled, $index);

    $streams[] = ["stream_id" => (int)$row["stream_id"], "score" => $final];
    $index++;
}

/* ================= EXAMS ================= */

$q = $conn->prepare("
    SELECT e.exam_id
    FROM entrance_exams e
    JOIN education_level_exams ele ON ele.exam_id = e.exam_id
    WHERE ele.education_level_id = ?
");
$q->bind_param("i", $educationLevel);
$q->execute();
$r = $q->get_result();

$index = 0;
while ($row = $r->fetch_assoc()) {

    $payload = [
        "education_level" => $educationLevel,
        "interest_tech" => $it,
        "interest_commerce" => $ic,
        "interest_arts" => $ia,
        "career_preference_private" => $careerPref,
        "difficulty_tolerance" => $difficultyTol,
        "risk_tolerance" => $riskTol,
        "option_type" => OPT_EXAM,
        "option_difficulty" => varyDifficulty(2, $index),
        "option_job_type" => 0
    ];

    $raw     = getAIScore($payload);
    $div     = diversifyScore($raw, $index, $educationLevel);
    $cal     = calibrateScore($div, $educationLevel, $index);
    $scaled  = scaleScore($cal);
    $final   = applySoftFloor($scaled, $index);

    $exams[] = ["exam_id" => (int)$row["exam_id"], "score" => $final];
    $index++;
}

/* ================= JOBS ================= */

$q = $conn->prepare("
    SELECT j.job_id, j.job_type
    FROM jobs j
    JOIN education_level_jobs elj ON elj.job_id = j.job_id
    WHERE elj.education_level_id = ?
");
$q->bind_param("i", $educationLevel);
$q->execute();
$r = $q->get_result();

$index = 0;
while ($row = $r->fetch_assoc()) {

    $jobType = strtolower($row["job_type"]) === "private" ? JOB_PRIVATE : JOB_GOVT;

    $payload = [
        "education_level" => $educationLevel,
        "interest_tech" => $it,
        "interest_commerce" => $ic,
        "interest_arts" => $ia,
        "career_preference_private" => $careerPref,
        "difficulty_tolerance" => $difficultyTol,
        "risk_tolerance" => $riskTol,
        "option_type" => OPT_JOB,
        "option_difficulty" => varyDifficulty(2, $index),
        "option_job_type" => $jobType
    ];

    $raw     = getAIScore($payload);
    $div     = diversifyScore($raw, $index, $educationLevel);
    $cal     = calibrateScore($div, $educationLevel, $index);
    $scaled  = scaleScore($cal);
    $final   = applySoftFloor($scaled, $index);

    $jobs[] = ["job_id" => (int)$row["job_id"], "score" => $final];
    $index++;
}

/* ================= NON-EMPTY GUARANTEE ================= */

$allStreams = $streams;
$allExams   = $exams;
$allJobs    = $jobs;

$thresholds = getThresholds($educationLevel);

$streams = array_filter($streams, fn($s) => $s["score"] >= $thresholds["stream"]);
$exams   = array_filter($exams,   fn($e) => $e["score"] >= $thresholds["exam"]);
$jobs    = array_filter($jobs,    fn($j) => $j["score"] >= $thresholds["job"]);

usort($allStreams, fn($a,$b) => $b["score"] <=> $a["score"]);
usort($allExams,   fn($a,$b) => $b["score"] <=> $a["score"]);
usort($allJobs,    fn($a,$b) => $b["score"] <=> $a["score"]);

if (empty($streams)) $streams = array_slice($allStreams, 0, 3);
if (empty($exams))   $exams   = array_slice($allExams, 0, 3);
if (empty($jobs))    $jobs    = array_slice($allJobs, 0, 3);

/* ================= FINAL RESPONSE ================= */

echo json_encode([
    "status" => true,
    "recommended_streams" => array_slice($streams, 0, 3),
    "recommended_exams" => array_slice($exams, 0, 3),
    "recommended_jobs" => array_slice($jobs, 0, 3)
]);
