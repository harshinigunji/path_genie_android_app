<?php
require_once "config/db.php";
header("Content-Type: application/json");

/* Enable SQL error reporting (disable in production) */
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

$question_id = (int)($_GET['question_id'] ?? 0);

if (!$question_id) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid question id"
    ]);
    exit;
}

$sql = "
SELECT 
    q.question_id,
    q.title,
    q.description,
    q.created_at,
    u.user_id,
    u.full_name
FROM forum_questions q
JOIN users u ON u.user_id = q.user_id
WHERE q.question_id = ?
";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "status" => false,
        "message" => "SQL prepare failed",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("i", $question_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Question not found"
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "question" => $result->fetch_assoc()
]);
