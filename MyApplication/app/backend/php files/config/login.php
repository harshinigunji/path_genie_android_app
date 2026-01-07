<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json");

include("config/db.php");
require_once("response.php");

$data = json_decode(file_get_contents("php://input"), true);

$email = trim($data['email'] ?? '');
$password = $data['password'] ?? '';

if (!$email || !$password) {
    error("Email and password are required");
}

$email = mysqli_real_escape_string($conn, $email);

/* FETCH USER */
$res = mysqli_query($conn, "
SELECT user_id, password_hash
FROM users
WHERE email = '$email'
");

if (mysqli_num_rows($res) === 0) {
    error("Invalid credentials");
}

$user = mysqli_fetch_assoc($res);

/* VERIFY PASSWORD */
if (!password_verify($password, $user['password_hash'])) {
    error("Invalid credentials");
}

/* GENERATE TOKEN */
$token = bin2hex(random_bytes(32));

mysqli_query($conn, "
UPDATE users SET auth_token = '$token'
WHERE user_id = {$user['user_id']}
");

success([
    "user_id" => $user['user_id'],
    "token" => $token
]);
