<?php
require_once "config/db.php";
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$user_id = (int)($data['user_id'] ?? 0);
$notification_id = (int)($data['notification_id'] ?? 0);
$mark_all = (bool)($data['mark_all'] ?? false);

if (!$user_id) {
    echo json_encode(["status" => false, "message" => "Invalid user id"]);
    exit;
}

if ($mark_all) {
    $stmt = $conn->prepare("UPDATE notifications SET is_read = 1 WHERE user_id = ?");
    $stmt->bind_param("i", $user_id);
} else if ($notification_id) {
    $stmt = $conn->prepare("UPDATE notifications SET is_read = 1 WHERE notification_id = ? AND user_id = ?");
    $stmt->bind_param("ii", $notification_id, $user_id);
} else {
    echo json_encode(["status" => false, "message" => "Missing notification_id or mark_all"]);
    exit;
}

$stmt->execute();

echo json_encode([
    "status" => true,
    "message" => "Notification(s) marked as read"
]);
