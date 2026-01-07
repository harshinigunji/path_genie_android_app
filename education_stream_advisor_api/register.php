<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type");
header("Content-Type: application/json");

include("config/db.php");
require_once("response.php");

$data = json_decode(file_get_contents("php://input"), true);

$name = trim($data['name'] ?? '');
$email = trim($data['email'] ?? '');
$password = $data['password'] ?? '';

if (!$name || !$email || !$password) {
    error("All fields are required");
}

$name = mysqli_real_escape_string($conn, $name);
$email = mysqli_real_escape_string($conn, $email);

/* CHECK EMAIL */
$check = mysqli_query($conn, "SELECT user_id FROM users WHERE email = '$email'");
if (mysqli_num_rows($check) > 0) {
    error("Email already registered");
}

/* CREATE USER */
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

$res = mysqli_query($conn, "
INSERT INTO users (full_name, email, password_hash, auth_provider)
VALUES ('$name', '$email', '$hashedPassword', 'EMAIL')
");

if (!$res) {
    error("Failed to register user");
}

success(["message" => "User registered successfully"]);
