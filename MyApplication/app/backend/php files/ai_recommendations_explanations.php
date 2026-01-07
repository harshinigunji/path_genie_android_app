<?php
require_once "config/db.php";

header("Content-Type: application/json");

/* ================= CONSTANTS ================= */

// Option types
define("OPT_STREAM", 1);
define("OPT_EXAM", 2);
define("OPT_JOB", 3);

// Job types
define("JOB_GOVT", 0);
define("JOB_PRIVATE", 1);

/* ================= HELPER FUNCTIONS ================= */

// Encode interest area into ML flags
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

// Map difficulty text → numeric
function mapDifficulty($level) {
    $level = strtolower(trim($level));
    return match ($level) {
        "easy" => 1,
        "medium" => 2,
        "hard" => 3,
        default => 2
    };
}

// Small variation to avoid same scores
function varyDifficulty($base, $index) {
    return max(1, min(3, $base + ($index % 2)));
}

// Call Python ML API
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
    return $json["recommendation_score"] ?? 0;
}

// Single-line dynamic explanation
function singleLineExplanation($score, $interestArea, $rank) {

    $interestText = match ($interestArea) {
        "Technology" => "technology interest",
        "Science" => "scientific aptitude",
        "Commerce" => "business inclination",
        "Arts" => "creative strengths",
        "Law" => "administrative and legal interest",
        default => "profile preferences"
    };

    if ($score >= 80) {
        $strength = "highly suitable";
    } elseif ($score >= 65) {
        $strength = "well aligned";
    } else {
        $strength = "reasonably suitable";
    }

    $rankText = match ($rank) {
        0 => "Top recommendation",
        1 => "Strong alternative",
        default => "Suitable option"
    };

    return "{$rankText} — {$strength} based on your {$interestText}.";
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

$q = $conn->prepare("
    SELECT stream_id, difficulty_level
    FROM streams
    WHERE education_level_id = ?
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
        "option_type" => OPT_STREAM,
        "option_difficulty" => varyDifficulty(
            mapDifficulty($row["difficulty_level"]),
            $index
        ),
        "option_job_type" => 0
    ];

    $streams[] = [
        "stream_id" => (int)$row["stream_id"],
        "score" => getAIScore($payload)
    ];

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

    $exams[] = [
        "exam_id" => (int)$row["exam_id"],
        "score" => getAIScore($payload)
    ];

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

    $jobType = strtolower($row["job_type"]) === "private"
        ? JOB_PRIVATE
        : JOB_GOVT;

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

    $jobs[] = [
        "job_id" => (int)$row["job_id"],
        "score" => getAIScore($payload)
    ];

    $index++;
}

/* ================= FILTER, SORT & LIMIT ================= */

// Thresholds
$streams = array_filter($streams, fn($s) => $s["score"] >= 70);
$exams   = array_filter($exams,   fn($e) => $e["score"] >= 65);
$jobs    = array_filter($jobs,    fn($j) => $j["score"] >= 65);

// Sort
usort($streams, fn($a,$b) => $b["score"] <=> $a["score"]);
usort($exams,   fn($a,$b) => $b["score"] <=> $a["score"]);
usort($jobs,    fn($a,$b) => $b["score"] <=> $a["score"]);

// Attach single-line explanations
foreach ($streams as $i => &$s) {
    $s["explanation"] = singleLineExplanation($s["score"], $interestArea, $i);
}
foreach ($exams as $i => &$e) {
    $e["explanation"] = singleLineExplanation($e["score"], $interestArea, $i);
}
foreach ($jobs as $i => &$j) {
    $j["explanation"] = singleLineExplanation($j["score"], $interestArea, $i);
}

// Final response (TOP 3 only)
echo json_encode([
    "status" => true,
    "recommended_streams" => array_slice($streams, 0, 3),
    "recommended_exams" => array_slice($exams, 0, 3),
    "recommended_jobs" => array_slice($jobs, 0, 3)
]);
