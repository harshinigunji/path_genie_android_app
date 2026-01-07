<?php
require_once "config/db.php";
header("Content-Type: application/json");

$education_level_id = (int)($_GET['education_level_id'] ?? 0);
$user_id = (int)($_GET['user_id'] ?? 0);
$my_questions = (int)($_GET['my_questions'] ?? 0);

$sql = "
SELECT q.question_id, q.title, q.description, q.status, q.created_at, q.education_level_id,
       u.full_name as author_name,
       (SELECT COUNT(*) FROM forum_answers a WHERE a.question_id = q.question_id) AS replies,
       (SELECT COUNT(*) FROM forum_likes l WHERE l.question_id = q.question_id) AS likes_count
FROM forum_questions q
LEFT JOIN users u ON u.user_id = q.user_id
";

$conditions = [];
$params = [];
$types = "";

if ($education_level_id) {
    $conditions[] = "q.education_level_id = ?";
    $params[] = $education_level_id;
    $types .= "i";
}

if ($my_questions && $user_id) {
    $conditions[] = "q.user_id = ?";
    $params[] = $user_id;
    $types .= "i";
}

if ($conditions) {
    $sql .= " WHERE " . implode(" AND ", $conditions);
}

$sql .= " ORDER BY q.created_at DESC";

$stmt = $conn->prepare($sql);
if ($params) {
    $stmt->bind_param($types, ...$params);
}
$stmt->execute();
$result = $stmt->get_result();

$questions = [];
while ($row = $result->fetch_assoc()) {
    // Check if current user liked this question
    if ($user_id) {
        $likeCheck = $conn->prepare("SELECT like_id FROM forum_likes WHERE user_id = ? AND question_id = ?");
        $likeCheck->bind_param("ii", $user_id, $row['question_id']);
        $likeCheck->execute();
        $row['user_liked'] = $likeCheck->get_result()->num_rows > 0;
    } else {
        $row['user_liked'] = false;
    }
    $questions[] = $row;
}

echo json_encode([
    "status" => true,
    "questions" => $questions
]);
