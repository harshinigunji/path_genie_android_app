<?php
require_once "config/db.php";
header("Content-Type: application/json");

// Create forum_replies table
$sql_replies = "
CREATE TABLE IF NOT EXISTS forum_replies (
    reply_id INT AUTO_INCREMENT PRIMARY KEY,
    answer_id INT NOT NULL,
    user_id INT NOT NULL,
    reply_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (answer_id) REFERENCES forum_answers(answer_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
)";

$success = true;
$error = "";

if (!$conn->query($sql_replies)) {
    $success = false;
    $error = $conn->error;
}

echo json_encode([
    "status" => $success,
    "message" => $success ? "Replies table created successfully" : "Error creating table",
    "error" => $error
]);
?>
