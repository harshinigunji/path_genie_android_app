<?php
/**
 * get_profile.php - Get user profile data
 */
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

include("config/db.php");

$user_id = intval($_GET['user_id'] ?? 0);

if ($user_id <= 0) {
    echo json_encode([
        "status" => false,
        "message" => "user_id is required"
    ]);
    exit;
}

// Get user profile
$result = mysqli_query($conn, "
    SELECT 
        user_id,
        full_name,
        email,
        phone,
        date_of_birth,
        education_level,
        current_school,
        board,
        last_exam_score,
        aspiring_career,
        profile_image,
        created_at
    FROM users
    WHERE user_id = $user_id
");

if (!$result || mysqli_num_rows($result) == 0) {
    echo json_encode([
        "status" => false,
        "message" => "User not found"
    ]);
    exit;
}

$user = mysqli_fetch_assoc($result);

// Get saved roadmaps count
$roadmapsCount = 0;
$countRes = mysqli_query($conn, "SELECT COUNT(*) as count FROM roadmaps WHERE user_id = $user_id");
if ($countRes) {
    $row = mysqli_fetch_assoc($countRes);
    $roadmapsCount = intval($row['count']);
}

// Get user roadmaps count
$tableCheck = mysqli_query($conn, "SHOW TABLES LIKE 'user_roadmaps'");
if (mysqli_num_rows($tableCheck) > 0) {
    $userCountRes = mysqli_query($conn, "SELECT COUNT(*) as count FROM user_roadmaps WHERE user_id = $user_id");
    if ($userCountRes) {
        $row = mysqli_fetch_assoc($userCountRes);
        $roadmapsCount += intval($row['count']);
    }
}

echo json_encode([
    "status" => true,
    "data" => [
        "user_id" => intval($user['user_id']),
        "full_name" => $user['full_name'] ?? "",
        "email" => $user['email'] ?? "",
        "phone" => $user['phone'] ?? "",
        "date_of_birth" => $user['date_of_birth'] ?? "",
        "education_level" => $user['education_level'] ?? "",
        "current_school" => $user['current_school'] ?? "",
        "board" => $user['board'] ?? "",
        "last_exam_score" => $user['last_exam_score'] ?? "",
        "aspiring_career" => $user['aspiring_career'] ?? "",
        "profile_image" => $user['profile_image'] ?? "",
        "saved_roadmaps_count" => $roadmapsCount,
        "member_since" => $user['created_at'] ?? ""
    ]
]);
?>
