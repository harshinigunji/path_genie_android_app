<?php
function authenticate($conn) {

    $headers = getallheaders();

    if (!isset($headers['Authorization'])) {
        echo json_encode([
            "status" => false,
            "message" => "Authorization header missing"
        ]);
        exit;
    }

    if (!preg_match('/Bearer\s(\S+)/', $headers['Authorization'], $matches)) {
        echo json_encode([
            "status" => false,
            "message" => "Invalid Authorization format"
        ]);
        exit;
    }

    $token = mysqli_real_escape_string($conn, $matches[1]);

    $res = mysqli_query($conn, "
        SELECT user_id FROM users
        WHERE auth_token = '$token'
    ");

    if (!$res || mysqli_num_rows($res) === 0) {
        echo json_encode([
            "status" => false,
            "message" => "Invalid or expired token"
        ]);
        exit;
    }

    $user = mysqli_fetch_assoc($res);
    return $user['user_id'];
}
