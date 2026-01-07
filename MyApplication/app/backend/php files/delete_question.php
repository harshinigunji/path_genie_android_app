<?php
require_once "config/db.php";
header("Content-Type: application/json");

$data = json_decode(file_get_contents("php://input"), true);

$question_id = (int)($data['question_id'] ?? 0);
$user_id = (int)($data['user_id'] ?? 0);

if (!$question_id || !$user_id) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid parameters"
    ]);
    exit;
}

// 1. Verify ownership
$checkSql = "SELECT question_id FROM forum_questions WHERE question_id = ? AND user_id = ?";
$checkStmt = $conn->prepare($checkSql);
$checkStmt->bind_param("ii", $question_id, $user_id);
$checkStmt->execute();
$checkResult = $checkStmt->get_result();

if ($checkResult->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Question not found or permission denied"
    ]);
    exit;
}
$checkStmt->close();

// 2. Delete Dependencies (Manual Cascade for safety)
// Delete replies to answers of this question
$deleteRepliesSql = "
    DELETE r FROM forum_replies r
    INNER JOIN forum_answers a ON r.answer_id = a.answer_id
    WHERE a.question_id = ?
";
$stmtReplies = $conn->prepare($deleteRepliesSql);
$stmtReplies->bind_param("i", $question_id);
$stmtReplies->execute();
$stmtReplies->close();

// Delete likes associated with the question or its answers
$deleteLikesSql = "
    DELETE FROM forum_likes 
    WHERE question_id = ? 
    OR answer_id IN (SELECT answer_id FROM forum_answers WHERE question_id = ?)
";
$stmtLikes = $conn->prepare($deleteLikesSql);
$stmtLikes->bind_param("ii", $question_id, $question_id);
$stmtLikes->execute();
$stmtLikes->close();

// Delete answers
$deleteAnswersSql = "DELETE FROM forum_answers WHERE question_id = ?";
$stmtAnswers = $conn->prepare($deleteAnswersSql);
$stmtAnswers->bind_param("i", $question_id);
$stmtAnswers->execute();
$stmtAnswers->close();

// 3. Delete the Question
$deleteQuestionSql = "DELETE FROM forum_questions WHERE question_id = ?";
$stmtQuestion = $conn->prepare($deleteQuestionSql);
$stmtQuestion->bind_param("i", $question_id);

if ($stmtQuestion->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Question deleted successfully"
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Failed to delete question",
        "error" => $conn->error
    ]);
}
$stmtQuestion->close();
?>
