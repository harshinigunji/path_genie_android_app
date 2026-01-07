<?php
require_once "config/db.php";
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$user_id = (int)($data['user_id'] ?? 0);
$question_id = (int)($data['question_id'] ?? 0);

if (!$user_id || !$question_id) {
    echo json_encode(["status" => false, "message" => "Invalid input"]);
    exit;
}

// Check if already liked
$checkStmt = $conn->prepare("SELECT like_id FROM forum_likes WHERE user_id = ? AND question_id = ?");
$checkStmt->bind_param("ii", $user_id, $question_id);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows > 0) {
    // Unlike - remove the like
    $deleteStmt = $conn->prepare("DELETE FROM forum_likes WHERE user_id = ? AND question_id = ?");
    $deleteStmt->bind_param("ii", $user_id, $question_id);
    $deleteStmt->execute();
    
    echo json_encode([
        "status" => true,
        "liked" => false,
        "message" => "Like removed"
    ]);
} else {
    // Like - add the like
    $insertStmt = $conn->prepare("INSERT INTO forum_likes (user_id, question_id) VALUES (?, ?)");
    $insertStmt->bind_param("ii", $user_id, $question_id);
    $insertStmt->execute();
    
    // Get question owner to send notification
    $ownerStmt = $conn->prepare("SELECT user_id, title FROM forum_questions WHERE question_id = ?");
    $ownerStmt->bind_param("i", $question_id);
    $ownerStmt->execute();
    $ownerResult = $ownerStmt->get_result();
    $question = $ownerResult->fetch_assoc();
    
    // Create notification for question owner (if not self)
    if ($question && $question['user_id'] != $user_id) {
        $notifyStmt = $conn->prepare("
            INSERT INTO notifications (user_id, type, reference_id, from_user_id, message)
            VALUES (?, 'like_question', ?, ?, ?)
        ");
        $message = "Someone liked your question";
        $notifyStmt->bind_param("iiis", $question['user_id'], $question_id, $user_id, $message);
        $notifyStmt->execute();
    }
    
    echo json_encode([
        "status" => true,
        "liked" => true,
        "message" => "Question liked"
    ]);
}
