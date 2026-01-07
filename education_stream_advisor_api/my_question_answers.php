<?php
require_once "config/db.php";
header("Content-Type: application/json");

/* Enable SQL error reporting (disable in production) */
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

$question_id = (int)($_GET['question_id'] ?? 0);
$user_id     = (int)($_GET['user_id'] ?? 0);

if (!$question_id || !$user_id) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid parameters"
    ]);
    exit;
}

/* Ownership check */
$checkSql = "
SELECT question_id 
FROM forum_questions
WHERE question_id = ? AND user_id = ?
";

$checkStmt = $conn->prepare($checkSql);

if (!$checkStmt) {
    echo json_encode([
        "status" => false,
        "message" => "SQL prepare failed",
        "error" => $conn->error
    ]);
    exit;
}

$checkStmt->bind_param("ii", $question_id, $user_id);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Access denied"
    ]);
    exit;
}

/* Fetch answers */
$sql = "
SELECT 
    a.answer_id,
    a.answer_text,
    a.created_at,
    u.user_id,
    u.full_name
FROM forum_answers a
JOIN users u ON u.user_id = a.user_id
WHERE a.question_id = ?
ORDER BY a.created_at ASC
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

$answers = [];
while ($row = $result->fetch_assoc()) {
    // Fetch replies for this answer
    $answer_id = $row['answer_id'];
    $replySql = "
        SELECT 
            r.reply_id,
            r.reply_text,
            r.created_at,
            u.full_name
        FROM forum_replies r
        JOIN users u ON u.user_id = r.user_id
        WHERE r.answer_id = $answer_id
        ORDER BY r.created_at ASC
    ";
    
    $replyResult = $conn->query($replySql);
    $replies = [];
    if ($replyResult) {
        while ($replyRow = $replyResult->fetch_assoc()) {
            $replies[] = $replyRow;
        }
    }
    
    $row['replies'] = $replies;
    $answers[] = $row;
}

echo json_encode([
    "status" => true,
    "answers" => $answers
]);
