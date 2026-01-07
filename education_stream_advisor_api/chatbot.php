<?php
// chatbot.php - Bridge to Python AI Backend
// Located in: education_stream_advisor_api/chatbot.php

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

// 1. Receive Input
$input = json_decode(file_get_contents("php://input"), true);
$message = $input['message'] ?? '';

// 2. Validate Input
if (empty($message)) {
    echo json_encode(["status" => true, "reply" => "Please say something."]);
    exit;
}

// 3. Call Python Backend
// IMPORTANT: Python server must be running on port 5000
$python_url = 'http://127.0.0.1:5000/chat'; 

$data = json_encode(["message" => $message]);

$ch = curl_init($python_url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    "Content-Type: application/json"
]);
curl_setopt($ch, CURLOPT_TIMEOUT, 30); // 30 second timeout

$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curl_error = curl_error($ch);
curl_close($ch);

// 4. Handle Response & Return to App
if ($curl_error) {
    // Python server is down or unreachable
    echo json_encode([
        "status" => false, 
        "reply" => "I'm currently sleeping (Server Offline). Please start the Python backend."
    ]);
} elseif ($http_code === 200 && $response) {
    // Success - Forward Python response directly
    // Expected Python Response: { "status": true, "reply": "..." }
    echo $response;
} else {
    // Python server returned an error (500, etc)
    echo json_encode([
        "status" => false, 
        "reply" => "I encountered an internal error. (Status: $http_code)"
    ]);
}
?>
