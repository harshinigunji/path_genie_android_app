<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

include("config/db.php");

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = $_POST['user_id'];
    
    if (isset($_FILES['image'])) {
        $target_dir = "uploads/";
        if (!file_exists($target_dir)) {
            mkdir($target_dir, 0777, true);
        }
        
        $original_filename = basename($_FILES["image"]["name"]);
        $file_extension = strtolower(pathinfo($original_filename, PATHINFO_EXTENSION));
        $new_filename = "profile_" . $user_id . "_" . time() . "." . $file_extension;
        $target_file = $target_dir . $new_filename;
        
        if (move_uploaded_file($_FILES["image"]["tmp_name"], $target_file)) {
            // Update database with full URL or relative path
            // Assuming config/db.php sets up $conn
            
            // Construct the full URL - UPDATE THIS DOMAIN to your actual server
            // For now we store the relative path or just the filename depending on how the app uses it
            // Let's store the relative path "uploads/filename"
            
            $image_url = $target_file; 
            
            $sql = "UPDATE users SET profile_image = '$image_url' WHERE user_id = $user_id";
            if (mysqli_query($conn, $sql)) {
                $response['status'] = true;
                $response['message'] = "Profile image uploaded successfully";
                $response['image_url'] = $image_url;
            } else {
                $response['status'] = false;
                $response['message'] = "Database update failed";
            }
        } else {
            $response['status'] = false;
            $response['message'] = "Failed to move uploaded file";
        }
    } else {
        $response['status'] = false;
        $response['message'] = "No image file provided";
    }
} else {
    $response['status'] = false;
    $response['message'] = "Invalid request method";
}

echo json_encode($response);
?>
