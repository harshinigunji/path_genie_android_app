<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

// API Key (Same as in ai_chat.php)
$api_key = "AIzaSyBEHG2tsCpO2rEs-hmd-gqdSXdqEmEXBD4"; 

// URL to list models
$url = "https://generativelanguage.googleapis.com/v1beta/models?key=" . $api_key;

// Initialize cURL session
$ch = curl_init($url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    "Content-Type: application/json"
]);

// Execute request
$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curl_error = curl_error($ch);
curl_close($ch);

if ($curl_error) {
    echo json_encode([
        "status" => false,
        "message" => "Network Error: " . $curl_error
    ]);
} else {
    $data = json_decode($response, true);
    echo json_encode([
        "status" => $http_code === 200,
        "http_code" => $http_code,
        "available_models" => $data['models'] ?? [],
        "raw_response" => $data
    ], JSON_PRETTY_PRINT);
}
?>
