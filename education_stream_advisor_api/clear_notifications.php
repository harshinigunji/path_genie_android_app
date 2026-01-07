<?php
require_once "config/db.php";
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$user_id = (int)($data['user_id'] ?? 0);

if (!$user_id) {
    echo json_encode(["status" => false, "message" => "Invalid user id"]);
    exit;
}

// Delete all notifications for this user
$stmt = $conn->prepare("DELETE FROM notifications WHERE user_id = ?");
$stmt->bind_param("i", $user_id);
$stmt->execute();

echo json_encode([
    "status" => true,
    "message" => "All notifications cleared",
    "deleted_count" => $stmt->affected_rows
]);
