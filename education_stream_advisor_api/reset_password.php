<?php
/**
 * reset_password.php - Reset user password with token
 * 
 * Called from the web form to update the password
 */
error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Content-Type: application/json");

// Handle preflight
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

include("config/db.php");

// Get data from POST (form submission) or JSON body
$token = '';
$new_password = '';
$confirm_password = '';

if ($_SERVER['CONTENT_TYPE'] === 'application/json' || 
    strpos($_SERVER['CONTENT_TYPE'] ?? '', 'application/json') !== false) {
    $data = json_decode(file_get_contents("php://input"), true);
    $token = trim($data['token'] ?? '');
    $new_password = $data['new_password'] ?? '';
    $confirm_password = $data['confirm_password'] ?? '';
} else {
    $token = trim($_POST['token'] ?? '');
    $new_password = $_POST['new_password'] ?? '';
    $confirm_password = $_POST['confirm_password'] ?? '';
}

// Validate inputs
if (empty($token)) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid reset link. Please request a new password reset."
    ]);
    exit;
}

if (empty($new_password)) {
    echo json_encode([
        "status" => false,
        "message" => "New password is required"
    ]);
    exit;
}

if (strlen($new_password) < 6) {
    echo json_encode([
        "status" => false,
        "message" => "Password must be at least 6 characters"
    ]);
    exit;
}

if ($new_password !== $confirm_password) {
    echo json_encode([
        "status" => false,
        "message" => "Passwords do not match"
    ]);
    exit;
}

// Find user with this token
$token_escaped = mysqli_real_escape_string($conn, $token);
$result = mysqli_query($conn, "
    SELECT user_id, reset_token_expires 
    FROM users 
    WHERE reset_token = '$token_escaped'
");

if (!$result || mysqli_num_rows($result) == 0) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid or expired reset link. Please request a new password reset."
    ]);
    exit;
}

$user = mysqli_fetch_assoc($result);
$user_id = $user['user_id'];
$expires = $user['reset_token_expires'];

// Check if token has expired
if (strtotime($expires) < time()) {
    // Clear expired token
    mysqli_query($conn, "UPDATE users SET reset_token = NULL, reset_token_expires = NULL WHERE user_id = $user_id");
    
    echo json_encode([
        "status" => false,
        "message" => "This reset link has expired. Please request a new password reset."
    ]);
    exit;
}

// Hash new password
$password_hash = password_hash($new_password, PASSWORD_DEFAULT);
$password_hash_escaped = mysqli_real_escape_string($conn, $password_hash);

// Update password and clear token
$update_result = mysqli_query($conn, "
    UPDATE users 
    SET password_hash = '$password_hash_escaped', 
        reset_token = NULL, 
        reset_token_expires = NULL 
    WHERE user_id = $user_id
");

if (!$update_result) {
    echo json_encode([
        "status" => false,
        "message" => "Failed to update password. Please try again."
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "message" => "Password has been reset successfully! You can now login with your new password."
]);
?>
