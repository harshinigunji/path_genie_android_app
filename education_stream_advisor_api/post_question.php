<?php
require_once "config/db.php";
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$user_id = (int)($data['user_id'] ?? 0);
$title = trim($data['title'] ?? '');
$description = trim($data['description'] ?? '');
$education_level_id = (int)($data['education_level_id'] ?? 0);

if (!$user_id || !$title || !$description || !$education_level_id) {
    echo json_encode(["status" => false, "message" => "Missing required fields"]);
    exit;
}

$stmt = $conn->prepare("
    INSERT INTO forum_questions (user_id, title, description, education_level_id)
    VALUES (?, ?, ?, ?)
");
$stmt->bind_param("issi", $user_id, $title, $description, $education_level_id);
$stmt->execute();

echo json_encode([
    "status" => true,
    "question_id" => $stmt->insert_id
]);
