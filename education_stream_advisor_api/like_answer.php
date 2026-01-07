<?php
require_once "config/db.php";
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$user_id = (int)($data['user_id'] ?? 0);
$answer_id = (int)($data['answer_id'] ?? 0);

if (!$user_id || !$answer_id) {
    echo json_encode(["status" => false, "message" => "Invalid input"]);
    exit;
}

// Check if already liked
$checkStmt = $conn->prepare("SELECT like_id FROM forum_likes WHERE user_id = ? AND answer_id = ?");
$checkStmt->bind_param("ii", $user_id, $answer_id);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows > 0) {
    // Unlike - remove the like
    $deleteStmt = $conn->prepare("DELETE FROM forum_likes WHERE user_id = ? AND answer_id = ?");
    $deleteStmt->bind_param("ii", $user_id, $answer_id);
    $deleteStmt->execute();
    
    echo json_encode([
        "status" => true,
        "liked" => false,
        "message" => "Like removed"
    ]);
} else {
    // Like - add the like
    $insertStmt = $conn->prepare("INSERT INTO forum_likes (user_id, answer_id) VALUES (?, ?)");
    $insertStmt->bind_param("ii", $user_id, $answer_id);
    $insertStmt->execute();
    
    // Get answer owner to send notification
    $ownerStmt = $conn->prepare("SELECT user_id FROM forum_answers WHERE answer_id = ?");
    $ownerStmt->bind_param("i", $answer_id);
    $ownerStmt->execute();
    $ownerResult = $ownerStmt->get_result();
    $answer = $ownerResult->fetch_assoc();
    
    // Create notification for answer owner (if not self)
    if ($answer && $answer['user_id'] != $user_id) {
        $notifyStmt = $conn->prepare("
            INSERT INTO notifications (user_id, type, reference_id, from_user_id, message)
            VALUES (?, 'like_answer', ?, ?, ?)
        ");
        $message = "Someone liked your answer";
        $notifyStmt->bind_param("iiis", $answer['user_id'], $answer_id, $user_id, $message);
        $notifyStmt->execute();
    }
    
    echo json_encode([
        "status" => true,
        "liked" => true,
        "message" => "Answer liked"
    ]);
}
