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
        
        // --- Notification Logic ---
        
        // 1. Get Answer Author and Question ID
        $authSql = "SELECT user_id, question_id FROM forum_answers WHERE answer_id = ?";
        $authStmt = $conn->prepare($authSql);
        $authStmt->bind_param("i", $answer_id);
        $authStmt->execute();
        $authResult = $authStmt->get_result();
        
        if ($res = $authResult->fetch_assoc()) {
            $author_id = $res['user_id'];
            $question_id = $res['question_id'];
            
            // 2. Only notify if replying to someone else
            if ($author_id != $user_id) {
                // Getsnder name (optional, or just use generic text)
                $sender_name = "Someone"; 
                $userSql = "SELECT full_name FROM users WHERE user_id = ?";
                $userStmt = $conn->prepare($userSql);
                $userStmt->bind_param("i", $user_id);
                $userStmt->execute();
                $uRes = $userStmt->get_result();
                if ($r = $uRes->fetch_assoc()) {
                    $sender_name = $r['full_name'];
                }
                $userStmt->close();

                $notif_msg = "$sender_name replied to your answer";
                $type = "reply";
                
                $notifSql = "INSERT INTO notifications (user_id, type, reference_id, from_user_id, message) VALUES (?, ?, ?, ?, ?)";
                $notifStmt = $conn->prepare($notifSql);
                $notifStmt->bind_param("isiis", $author_id, $type, $question_id, $user_id, $notif_msg);
                $notifStmt->execute();
                $notifStmt->close();
            }
        }
        $authStmt->close();
        
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
