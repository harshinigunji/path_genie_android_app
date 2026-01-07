<?php
require_once "config/db.php";
require_once "send_push_notification.php";
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$question_id = (int)($data['question_id'] ?? 0);
$user_id = (int)($data['user_id'] ?? 0);
$answer_text = trim($data['answer_text'] ?? '');

if (!$question_id || !$user_id || !$answer_text) {
    echo json_encode(["status" => false, "message" => "Invalid input"]);
    exit;
}

// Insert answer
$stmt = $conn->prepare("
    INSERT INTO forum_answers (question_id, user_id, answer_text)
    VALUES (?, ?, ?)
");
$stmt->bind_param("iis", $question_id, $user_id, $answer_text);
$stmt->execute();
$answer_id = $stmt->insert_id;

// Update question status
$conn->query("
    UPDATE forum_questions
    SET status = 'ANSWERED'
    WHERE question_id = $question_id
");

// Get question owner for notification
$ownerStmt = $conn->prepare("SELECT user_id, title FROM forum_questions WHERE question_id = ?");
$ownerStmt->bind_param("i", $question_id);
$ownerStmt->execute();
$ownerResult = $ownerStmt->get_result();
$question = $ownerResult->fetch_assoc();

// Create notification for question owner (if not self)
if ($question && $question['user_id'] != $user_id) {
    $notifyStmt = $conn->prepare("
        INSERT INTO notifications (user_id, type, reference_id, from_user_id, message)
        VALUES (?, 'answer', ?, ?, ?)
    ");
    $message = "Someone answered your question: " . substr($question['title'], 0, 50);
    $notifyStmt->bind_param("iiis", $question['user_id'], $question_id, $user_id, $message);
    $notifyStmt->execute();
    
    // Send push notification
    $fcmStmt = $conn->prepare("SELECT fcm_token FROM users WHERE user_id = ?");
    $fcmStmt->bind_param("i", $question['user_id']);
    $fcmStmt->execute();
    $fcmResult = $fcmStmt->get_result();
    $fcmRow = $fcmResult->fetch_assoc();
    
    if ($fcmRow && !empty($fcmRow['fcm_token'])) {
        sendPushNotification(
            $fcmRow['fcm_token'],
            "New Answer!",
            $message,
            ["question_id" => $question_id]
        );
    }
}

echo json_encode([
    "status" => true,
    "answer_id" => $answer_id
]);
