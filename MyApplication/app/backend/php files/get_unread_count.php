<?php
require_once "config/db.php";
header("Content-Type: application/json");

$user_id = (int)($_GET['user_id'] ?? 0);

if (!$user_id) {
    echo json_encode(["status" => false, "message" => "Invalid user id"]);
    exit;
}

$stmt = $conn->prepare("SELECT COUNT(*) as unread_count FROM notifications WHERE user_id = ? AND is_read = 0");
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

echo json_encode([
    "status" => true,
    "unread_count" => (int)$row['unread_count']
]);
