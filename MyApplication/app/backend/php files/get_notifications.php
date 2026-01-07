<?php
require_once "config/db.php";
header("Content-Type: application/json");

$user_id = (int)($_GET['user_id'] ?? 0);
$limit = (int)($_GET['limit'] ?? 20);
$offset = (int)($_GET['offset'] ?? 0);

if (!$user_id) {
    echo json_encode(["status" => false, "message" => "Invalid user id"]);
    exit;
}

$sql = "
SELECT 
    n.notification_id,
    n.type,
    n.reference_id,
    n.message,
    n.is_read,
    n.created_at,
    u.full_name as from_user_name,
    CASE 
        WHEN n.type = 'answer' THEN (SELECT title FROM forum_questions WHERE question_id = n.reference_id)
        WHEN n.type = 'reply' THEN (SELECT title FROM forum_questions WHERE question_id = n.reference_id)
        WHEN n.type = 'like_question' THEN (SELECT title FROM forum_questions WHERE question_id = n.reference_id)
        WHEN n.type = 'like_answer' THEN (SELECT title FROM forum_questions q JOIN forum_answers a ON q.question_id = a.question_id WHERE a.answer_id = n.reference_id)
        ELSE NULL
    END as question_title,
    CASE 
        WHEN n.type IN ('answer', 'like_question', 'reply') THEN n.reference_id
        WHEN n.type = 'like_answer' THEN (SELECT question_id FROM forum_answers WHERE answer_id = n.reference_id)
        ELSE NULL
    END as question_id
FROM notifications n
JOIN users u ON u.user_id = n.from_user_id
WHERE n.user_id = ?
ORDER BY n.created_at DESC
LIMIT ? OFFSET ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("iii", $user_id, $limit, $offset);
$stmt->execute();
$result = $stmt->get_result();

$notifications = [];
while ($row = $result->fetch_assoc()) {
    $notifications[] = $row;
}

echo json_encode([
    "status" => true,
    "notifications" => $notifications
]);
