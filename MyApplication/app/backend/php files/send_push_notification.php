<?php
/**
 * send_push_notification.php - Helper to send FCM push notifications using V1 API
 * 
 * Usage: require this file and call sendPushNotification($fcm_token, $title, $body, $data)
 */

/**
 * Get OAuth2 access token from service account
 */
function getAccessToken() {
    $serviceAccountPath = __DIR__ . '/config/firebase_service_account.json';
    
    if (!file_exists($serviceAccountPath)) {
        error_log("FCM: Service account file not found");
        return null;
    }
    
    $serviceAccount = json_decode(file_get_contents($serviceAccountPath), true);
    
    // Create JWT
    $header = base64_encode(json_encode(['typ' => 'JWT', 'alg' => 'RS256']));
    
    $now = time();
    $payload = base64_encode(json_encode([
        'iss' => $serviceAccount['client_email'],
        'scope' => 'https://www.googleapis.com/auth/firebase.messaging',
        'aud' => 'https://oauth2.googleapis.com/token',
        'iat' => $now,
        'exp' => $now + 3600
    ]));
    
    $signatureInput = $header . '.' . $payload;
    
    // Sign with private key
    $privateKey = openssl_pkey_get_private($serviceAccount['private_key']);
    openssl_sign($signatureInput, $signature, $privateKey, OPENSSL_ALGO_SHA256);
    $signature = base64_encode($signature);
    
    // URL-safe base64
    $jwt = str_replace(['+', '/', '='], ['-', '_', ''], $header) . '.' .
           str_replace(['+', '/', '='], ['-', '_', ''], $payload) . '.' .
           str_replace(['+', '/', '='], ['-', '_', ''], $signature);
    
    // Exchange JWT for access token
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, 'https://oauth2.googleapis.com/token');
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query([
        'grant_type' => 'urn:ietf:params:oauth:grant-type:jwt-bearer',
        'assertion' => $jwt
    ]));
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/x-www-form-urlencoded']);
    
    $response = curl_exec($ch);
    curl_close($ch);
    
    $tokenData = json_decode($response, true);
    return $tokenData['access_token'] ?? null;
}

/**
 * Send push notification to a device
 * 
 * @param string $fcm_token Device FCM token
 * @param string $title Notification title
 * @param string $body Notification body
 * @param array $data Additional data payload
 * @return bool Success status
 */
function sendPushNotification($fcm_token, $title, $body, $data = []) {
    if (empty($fcm_token)) {
        error_log("FCM: Empty FCM token");
        return false;
    }
    
    $accessToken = getAccessToken();
    if (!$accessToken) {
        error_log("FCM: Failed to get access token");
        return false;
    }
    
    $projectId = 'educationstreamadvisor';
    $url = "https://fcm.googleapis.com/v1/projects/{$projectId}/messages:send";
    
    $message = [
        'message' => [
            'token' => $fcm_token,
            'notification' => [
                'title' => $title,
                'body' => $body
            ],
            'android' => [
                'priority' => 'high',
                'notification' => [
                    'sound' => 'default',
                    'click_action' => 'OPEN_NOTIFICATIONS'
                ]
            ]
        ]
    ];
    
    // Add data payload if provided
    if (!empty($data)) {
        $message['message']['data'] = array_map('strval', $data);
    }
    
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($message));
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, [
        'Authorization: Bearer ' . $accessToken,
        'Content-Type: application/json'
    ]);
    
    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
    
    if ($httpCode == 200) {
        error_log("FCM: Notification sent successfully");
        return true;
    } else {
        error_log("FCM: Failed to send notification. Response: " . $response);
        return false;
    }
}
?>
