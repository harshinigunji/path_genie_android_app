<?php
// Prevent any output before JSON
ob_start();
error_reporting(0); // Supress warnings/errors
ini_set('display_errors', 0);

header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

$response = array();

try {
    // Check if db config exists
    if (!file_exists("config/db.php")) {
        throw new Exception("Database config file not found");
    }

    require_once("config/db.php");

    if (!isset($conn)) {
        throw new Exception("Database connection variable not set");
    }

    if ($conn->connect_error) {
        throw new Exception("Database connection failed: " . $conn->connect_error);
    }

    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        if (isset($_POST['user_id'])) {
            $user_id = intval($_POST['user_id']);

            // First, get the current image path to delete the file
            $get_image_query = "SELECT profile_image FROM users WHERE user_id = ?";
            
            if ($stmt_get = $conn->prepare($get_image_query)) {
                $stmt_get->bind_param("i", $user_id);
                $stmt_get->execute();
                $stmt_get->bind_result($current_image);
                $stmt_get->fetch();
                $stmt_get->close();

                // Update database to remove reference
                $update_query = "UPDATE users SET profile_image = NULL WHERE user_id = ?";
                
                if ($stmt = $conn->prepare($update_query)) {
                    $stmt->bind_param("i", $user_id);

                    if ($stmt->execute()) {
                        // Delete the file if it exists and looks like a relative path
                        if (!empty($current_image) && file_exists($current_image)) {
                            unlink($current_image);
                        }
                        
                        $response['status'] = true;
                        $response['message'] = "Profile picture removed successfully";
                    } else {
                        throw new Exception("Failed to update database");
                    }
                    $stmt->close();
                } else {
                    throw new Exception("Database error: prepare update failed");
                }
            } else {
                throw new Exception("Database error: prepare select failed");
            }
        } else {
            $response['status'] = false;
            $response['message'] = "Missing user_id";
        }
    } else {
        $response['status'] = false;
        $response['message'] = "Invalid request method";
    }

} catch (Exception $e) {
    $response['status'] = false;
    $response['message'] = "Server Error: " . $e->getMessage();
}

// Clean buffer and output JSON
ob_end_clean();
echo json_encode($response);

if (isset($conn)) {
    $conn->close();
}
?>
