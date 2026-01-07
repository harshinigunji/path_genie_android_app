<?php
/**
 * save_fcm_token.php - Save user's FCM token for push notifications
 */
require_once "config/db.php";
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type");
header("Access-Control-Allow-Methods: POST, OPTIONS");

// Handle preflight
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

$data = json_decode(file_get_contents("php://input"), true);

$user_id = (int)($data['user_id'] ?? 0);
$fcm_token = trim($data['fcm_token'] ?? '');

if (!$user_id || !$fcm_token) {
    echo json_encode(["status" => false, "message" => "User ID and FCM token are required"]);
    exit;
}

// Update user's FCM token
$stmt = $conn->prepare("UPDATE users SET fcm_token = ? WHERE user_id = ?");
$stmt->bind_param("si", $fcm_token, $user_id);

if ($stmt->execute()) {
    echo json_encode(["status" => true, "message" => "FCM token saved successfully"]);
} else {
    echo json_encode(["status" => false, "message" => "Failed to save FCM token"]);
}
?>
