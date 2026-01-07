<?php
/**
 * login_firebase.php - Get user data after Firebase authentication
 * 
 * This endpoint is called AFTER Firebase Auth has verified the password.
 * It only fetches user data by email, no password verification needed.
 */
error_reporting(E_ALL);
ini_set('display_errors', 0);

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Content-Type: application/json");

// Handle preflight
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

include("config/db.php");
require_once("response.php");

$data = json_decode(file_get_contents("php://input"), true);

$email = trim($data['email'] ?? '');

if (empty($email)) {
    error("Email is required");
}

$email = mysqli_real_escape_string($conn, $email);

/* FETCH USER BY EMAIL */
$res = mysqli_query($conn, "
    SELECT user_id, full_name, email
    FROM users
    WHERE email = '$email'
");

if (!$res || mysqli_num_rows($res) === 0) {
    error("User not found");
}

$user = mysqli_fetch_assoc($res);

/* GENERATE TOKEN */
$token = bin2hex(random_bytes(32));

mysqli_query($conn, "
    UPDATE users SET auth_token = '$token'
    WHERE user_id = {$user['user_id']}
");

success([
    "user_id" => $user['user_id'],
    "token" => $token,
    "name" => $user['full_name']
]);
?>
