<?php
/**
 * forgot_password.php - Send password reset email
 * 
 * Uses PHPMailer with Gmail SMTP to send emails
 */
error_reporting(E_ALL);
ini_set('display_errors', 0);  // Disable display errors for JSON response

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Content-Type: application/json");

// Handle preflight
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

// Load PHPMailer
require 'vendor/autoload.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

include("config/db.php");

// ============================================
// EMAIL CONFIGURATION
// ============================================
define('EMAIL_ENABLED', true);
define('SMTP_HOST', 'smtp.gmail.com');
define('SMTP_PORT', 587);
define('SMTP_USERNAME', 'harshinigunji8008@gmail.com');
define('SMTP_PASSWORD', 'yfweaxeaohgfkhxh');
define('FROM_NAME', 'Education Stream Advisor');
define('RESET_BASE_URL', 'http://localhost/education_stream_advisor_api/reset_password_form.php');

// ============================================

$data = json_decode(file_get_contents("php://input"), true);
$email = trim($data['email'] ?? '');

// Validate email
if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email is required"
    ]);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "status" => false,
        "message" => "Please enter a valid email address"
    ]);
    exit;
}

// Check if email exists in database
$email_escaped = mysqli_real_escape_string($conn, $email);
$result = mysqli_query($conn, "SELECT user_id, full_name FROM users WHERE email = '$email_escaped'");

if (!$result || mysqli_num_rows($result) == 0) {
    // Don't reveal if email exists or not (security)
    echo json_encode([
        "status" => true,
        "message" => "If this email is registered, you will receive a password reset link shortly."
    ]);
    exit;
}

$user = mysqli_fetch_assoc($result);
$user_id = $user['user_id'];
$user_name = $user['full_name'];

// Generate secure reset token
$token = bin2hex(random_bytes(32));
$expires = date('Y-m-d H:i:s', strtotime('+1 hour'));

// Store token in database
$token_escaped = mysqli_real_escape_string($conn, $token);
$update_result = mysqli_query($conn, "
    UPDATE users 
    SET reset_token = '$token_escaped', reset_token_expires = '$expires' 
    WHERE user_id = $user_id
");

if (!$update_result) {
    echo json_encode([
        "status" => false,
        "message" => "Failed to generate reset token. Please try again."
    ]);
    exit;
}

// Create reset link
$reset_link = RESET_BASE_URL . "?token=" . $token;

// Send email
if (EMAIL_ENABLED) {
    $mail = new PHPMailer(true);
    
    try {
        // Server settings
        $mail->isSMTP();
        $mail->Host       = SMTP_HOST;
        $mail->SMTPAuth   = true;
        $mail->Username   = SMTP_USERNAME;
        $mail->Password   = SMTP_PASSWORD;
        $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
        $mail->Port       = SMTP_PORT;
        
        // Fix for local XAMPP - disable SSL verification
        $mail->SMTPOptions = array(
            'ssl' => array(
                'verify_peer' => false,
                'verify_peer_name' => false,
                'allow_self_signed' => true
            )
        );
        
        // Recipients
        $mail->setFrom(SMTP_USERNAME, FROM_NAME);
        $mail->addAddress($email, $user_name);
        
        // Content
        $mail->isHTML(true);
        $mail->Subject = 'Password Reset Request - Education Stream Advisor';
        $mail->Body    = "
            <html>
            <body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>
                <div style='max-width: 600px; margin: 0 auto; padding: 20px;'>
                    <h2 style='color: #2563EB;'>Password Reset Request</h2>
                    <p>Hello <strong>{$user_name}</strong>,</p>
                    <p>We received a request to reset your password for your Education Stream Advisor account.</p>
                    <p>Click the button below to reset your password:</p>
                    <p style='text-align: center; margin: 30px 0;'>
                        <a href='{$reset_link}' 
                           style='background-color: #2563EB; color: white; padding: 12px 30px; 
                                  text-decoration: none; border-radius: 8px; font-weight: bold;'>
                            Reset Password
                        </a>
                    </p>
                    <p>Or copy and paste this link in your browser:</p>
                    <p style='background-color: #f5f5f5; padding: 10px; word-break: break-all;'>
                        {$reset_link}
                    </p>
                    <p><strong>This link will expire in 1 hour.</strong></p>
                    <p>If you didn't request this password reset, please ignore this email.</p>
                    <hr style='border: none; border-top: 1px solid #eee; margin: 30px 0;'>
                    <p style='color: #666; font-size: 12px;'>
                        This email was sent by Education Stream Advisor App.
                    </p>
                </div>
            </body>
            </html>
        ";
        $mail->AltBody = "Hello {$user_name},\n\nClick this link to reset your password: {$reset_link}\n\nThis link expires in 1 hour.";
        
        $mail->send();
        
        echo json_encode([
            "status" => true,
            "message" => "Password reset link has been sent to your email."
        ]);
        
    } catch (Exception $e) {
        echo json_encode([
            "status" => false,
            "message" => "Failed to send email. Error: " . $mail->ErrorInfo
        ]);
    }
    
} else {
    echo json_encode([
        "status" => true,
        "message" => "Password reset link has been sent to your email.",
        "debug_reset_link" => $reset_link
    ]);
}
?>
