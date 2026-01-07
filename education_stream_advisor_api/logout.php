<?php
header("Content-Type: application/json");

include("config/db.php");
include("response.php");

$data = json_decode(file_get_contents("php://input"), true);
$user_id = intval($data['user_id'] ?? 0);

if (!$user_id) {
    error("User ID required");
}

mysqli_query($conn, "
    UPDATE users SET auth_token=NULL WHERE user_id=$user_id
");

success(["message" => "Logged out successfully"]);
