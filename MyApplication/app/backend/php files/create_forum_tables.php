<?php
require_once "config/db.php";
header("Content-Type: application/json");

// Create forum_likes table
$sql_likes = "
CREATE TABLE IF NOT EXISTS forum_likes (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    question_id INT DEFAULT NULL,
    answer_id INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_question_like (user_id, question_id),
    UNIQUE KEY unique_answer_like (user_id, answer_id)
)";

// Create notifications table
$sql_notifications = "
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    reference_id INT NOT NULL,
    from_user_id INT NOT NULL,
    message TEXT,
    is_read TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_read (user_id, is_read)
)";

$success = true;
$errors = [];

if (!$conn->query($sql_likes)) {
    $success = false;
    $errors[] = "forum_likes: " . $conn->error;
}

if (!$conn->query($sql_notifications)) {
    $success = false;
    $errors[] = "notifications: " . $conn->error;
}

echo json_encode([
    "status" => $success,
    "message" => $success ? "Tables created successfully" : "Some errors occurred",
    "errors" => $errors
]);
