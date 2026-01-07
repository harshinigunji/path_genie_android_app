<?php
require_once "config/db.php";
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$answer_id = (int)($data['answer_id'] ?? 0);
$user_id = (int)($data['user_id'] ?? 0);
$reply_text = trim($data['reply_text'] ?? '');

if (!$answer_id || !$user_id || empty($reply_text)) {
    echo json_encode([
        "status" => false,
        "message" => "All fields are required"
    ]);
    exit;
}

$sql = "INSERT INTO forum_replies (answer_id, user_id, reply_text) VALUES (?, ?, ?)";
$stmt = $conn->prepare($sql);

if ($stmt) {
    $stmt->bind_param("iis", $answer_id, $user_id, $reply_text);
    if ($stmt->execute()) {
        echo json_encode([
            "status" => true,
            "message" => "Reply posted successfully"
        ]);
        
        // Optional: Notify the answer author?
        // Notification logic would go here
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Failed to post reply",
            "error" => $stmt->error
        ]);
    }
    $stmt->close();
} else {
    echo json_encode([
        "status" => false,
        "message" => "Database error"
    ]);
}
?>
