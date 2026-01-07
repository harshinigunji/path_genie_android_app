<?php
// Prevent any output before JSON
ob_start();
error_reporting(0); // Suppress warnings/errors
ini_set('display_errors', 0);

header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

$response = array();

try {
    // Check if db config exists
    if (!file_exists("config/db.php")) {
        throw new Exception("Database config file not found");
    }

    require_once("config/db.php");

    if (!isset($conn)) {
        throw new Exception("Database connection variable not set");
    }

    if ($conn->connect_error) {
        throw new Exception("Database connection failed: " . $conn->connect_error);
    }

    $input = file_get_contents("php://input");
    $data = json_decode($input, true);

    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Invalid JSON input");
    }

    $user_id = intval($data['user_id'] ?? 0);
    $current_password = $data['current_password'] ?? '';
    $new_password = $data['new_password'] ?? '';
    $confirm_password = $data['confirm_password'] ?? '';

    if ($user_id <= 0) {
        throw new Exception("user_id is required");
    }

    if (empty($current_password)) {
        throw new Exception("Current password is required");
    }

    if (empty($new_password)) {
        throw new Exception("New password is required");
    }

    if (strlen($new_password) < 8) {
        throw new Exception("Password must be at least 8 characters");
    }

    if ($new_password !== $confirm_password) {
        throw new Exception("Passwords do not match");
    }

    // Get current password hash
    $stmt = $conn->prepare("SELECT password_hash FROM users WHERE user_id = ?");
    if (!$stmt) {
        throw new Exception("Database prepare failed: " . $conn->error);
    }
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        throw new Exception("User not found");
    }

    $user = $result->fetch_assoc();
    $stmt->close();

    // Verify current password
    if (!password_verify($current_password, $user['password_hash'])) {
        throw new Exception("Current password is incorrect");
    }

    // Hash new password
    $newPasswordHash = password_hash($new_password, PASSWORD_DEFAULT);

    // Update password
    $updateStmt = $conn->prepare("UPDATE users SET password_hash = ? WHERE user_id = ?");
    if (!$updateStmt) {
        throw new Exception("Database update prepare failed: " . $conn->error);
    }
    $updateStmt->bind_param("si", $newPasswordHash, $user_id);
    
    if ($updateStmt->execute()) {
        $response['status'] = true;
        $response['message'] = "Password changed successfully";
    } else {
        throw new Exception("Failed to update password");
    }
    $updateStmt->close();

} catch (Exception $e) {
    $response['status'] = false;
    $response['message'] = $e->getMessage();
}

// Clean buffer and output JSON
ob_end_clean();
echo json_encode($response);

if (isset($conn)) {
    $conn->close();
}
?>
