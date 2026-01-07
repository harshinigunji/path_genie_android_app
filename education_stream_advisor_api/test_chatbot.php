<?php
// test_chatbot.php
// Run this file in your browser: http://<server-ip>/education_stream_advisor_api/test_chatbot.php

header("Content-Type: text/plain");

echo "=== DIAGNOSTIC TEST START ===\n\n";

$python_url = 'http://127.0.0.1:5000/chat';
echo "Target URL: $python_url\n";

$data = json_encode(["message" => "Test Message"]);
echo "Payload: $data\n\n";

$ch = curl_init($python_url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
curl_setopt($ch, CURLOPT_HTTPHEADER, ["Content-Type: application/json"]);
curl_setopt($ch, CURLOPT_TIMEOUT, 10);
curl_setopt($ch, CURLOPT_VERBOSE, true);

$verbose = fopen('php://temp', 'w+');
curl_setopt($ch, CURLOPT_STDERR, $verbose);

echo "Attempting connection...\n";

$response = curl_exec($ch);
$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curl_error = curl_error($ch);
$curl_errno = curl_errno($ch);

curl_close($ch);

echo "HTTP Code: $http_code\n";
echo "cURL Error Code: $curl_errno\n";
echo "cURL Error Message: $curl_error\n\n";

echo "Response Body:\n";
echo $response ? $response : "(Empty Response)";

echo "\n\n=== VERBOSE LOG ===\n";
rewind($verbose);
$verboseLog = stream_get_contents($verbose);
echo $verboseLog;

echo "\n=== DIAGNOSTIC TEST END ===\n";
?>
