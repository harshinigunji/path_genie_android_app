<?php
/**
 * update_profile.php - Update user profile data
 */
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

include("config/db.php");

$data = json_decode(file_get_contents("php://input"), true);

$user_id = intval($data['user_id'] ?? 0);
$full_name = trim($data['full_name'] ?? '');
$email = trim($data['email'] ?? '');
$phone = trim($data['phone'] ?? '');
$date_of_birth = trim($data['date_of_birth'] ?? '');
$education_level = trim($data['education_level'] ?? '');
$current_school = trim($data['current_school'] ?? '');
$board = trim($data['board'] ?? '');
$last_exam_score = trim($data['last_exam_score'] ?? '');
$aspiring_career = trim($data['aspiring_career'] ?? '');

if ($user_id <= 0) {
    echo json_encode([
        "status" => false,
        "message" => "user_id is required"
    ]);
    exit;
}

if (empty($full_name)) {
    echo json_encode([
        "status" => false,
        "message" => "Full name is required"
    ]);
    exit;
}

// Check if email is already used by another user
if (!empty($email)) {
    $emailCheck = mysqli_query($conn, "SELECT user_id FROM users WHERE email = '$email' AND user_id != $user_id");
    if ($emailCheck && mysqli_num_rows($emailCheck) > 0) {
        echo json_encode([
            "status" => false,
            "message" => "Email is already in use"
        ]);
        exit;
    }
}

// Escape strings
$full_name = mysqli_real_escape_string($conn, $full_name);
$email = mysqli_real_escape_string($conn, $email);
$phone = mysqli_real_escape_string($conn, $phone);
$date_of_birth = mysqli_real_escape_string($conn, $date_of_birth);
$education_level = mysqli_real_escape_string($conn, $education_level);
$current_school = mysqli_real_escape_string($conn, $current_school);
$board = mysqli_real_escape_string($conn, $board);
$last_exam_score = mysqli_real_escape_string($conn, $last_exam_score);
$aspiring_career = mysqli_real_escape_string($conn, $aspiring_career);

// Update user profile
$result = mysqli_query($conn, "
    UPDATE users SET
        full_name = '$full_name',
        email = '$email',
        phone = '$phone',
        date_of_birth = " . ($date_of_birth ? "'$date_of_birth'" : "NULL") . ",
        education_level = '$education_level',
        current_school = '$current_school',
        board = '$board',
        last_exam_score = '$last_exam_score',
        aspiring_career = '$aspiring_career'
    WHERE user_id = $user_id
");

if (!$result) {
    echo json_encode([
        "status" => false,
        "message" => "Failed to update profile: " . mysqli_error($conn)
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "message" => "Profile updated successfully"
]);
?>
