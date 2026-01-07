<?php
require_once "config/db.php";
header("Content-Type: application/json");

$user_id = (int)($_GET['user_id'] ?? 0);

$stmt = $conn->prepare("
    SELECT q.question_id, q.title, q.status, q.created_at,
           (SELECT COUNT(*) FROM forum_answers a WHERE a.question_id = q.question_id) AS replies
    FROM forum_questions q
    WHERE q.user_id = ?
    ORDER BY q.created_at DESC
");
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$questions = [];
while ($row = $result->fetch_assoc()) {
    $questions[] = $row;
}

echo json_encode([
    "status" => true,
    "questions" => $questions
]);
