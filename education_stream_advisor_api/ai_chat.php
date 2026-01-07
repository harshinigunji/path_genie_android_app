<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

$data = json_decode(file_get_contents("php://input"), true);

$user_id = $data['user_id'] ?? 0;
$message = trim($data['message'] ?? '');

$api_key = "AIzaSyBEHG2tsCpO2rEs-hmd-gqdSXdqEmEXBD4";

if (empty($message)) {
    echo json_encode(["status" => false, "message" => "Empty message"]);
    exit;
}

// ✅ CORRECT ENDPOINT
$url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" . $api_key;

// System prompt (merged)
$system_instruction = "You are EduBot, an intelligent Education Stream Advisor.
Help students choose streams, exams, and careers.
Keep answers short, friendly, and student-friendly.
Use bullet points when listing.
If asked about the app, say you are the AI assistant for the Education Stream Advisor App.";

$postData = [
    "contents" => [
        [
            "role" => "user",
            "parts" => [
                [
                    "text" => $system_instruction . "\n\nUser question:\n" . $message
                ]
            ]
        ]
    ]
];

$ch = curl_init($url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($postData));
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    "Content-Type: application/json"
]);

$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curl_error = curl_error($ch);
curl_close($ch);

$reply = "";

if ($curl_error) {
    $reply = "Network error. Please try again.";
} elseif ($http_code !== 200) {
    $error_data = json_decode($response, true);
    $error_msg = $error_data['error']['message'] ?? "Unknown error";
    $reply = "AI Error ($http_code): $error_msg";
} else {
    $result = json_decode($response, true);
    $reply = $result['candidates'][0]['content']['parts'][0]['text']
        ?? "Sorry, I couldn't understand that.";
}

echo json_encode([
    "status" => true,
    "data" => [
        "reply" => $reply,
        "timestamp" => date("h:i A")
    ]
]);
?>
