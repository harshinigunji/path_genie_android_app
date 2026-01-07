<?php
/**
 * delete_roadmap.php - Delete a saved roadmap
 * 
 * Tables:
 * - roadmaps: SYSTEM roadmaps (has roadmap_id, user_id, start_stream_id, target_job_id)
 * - user_roadmaps: USER roadmaps (has id/roadmap_id, user_id, title, etc)
 * - user_roadmap_steps: Steps for user roadmaps
 * - roadmap_steps: Steps for system roadmaps
 */

error_reporting(E_ALL);
ini_set('display_errors', 0);

header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: POST, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    exit(0);
}

include("config/db.php");
require_once("response.php");

// Get input data
$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['roadmap_id']) || !isset($data['user_id'])) {
    error('Missing roadmap_id or user_id');
}

$roadmap_id = intval($data['roadmap_id']);
$user_id = intval($data['user_id']);
$roadmap_type = isset($data['roadmap_type']) ? $data['roadmap_type'] : 'SYSTEM';

// Log for debugging
error_log("delete_roadmap: roadmap_id=$roadmap_id, user_id=$user_id, type=$roadmap_type");

$deleted = false;

if ($roadmap_type === 'USER') {
    // Delete from user_roadmaps table
    
    // First check which column name is used (id or roadmap_id)
    $columnsRes = mysqli_query($conn, "DESCRIBE user_roadmaps");
    $idColumn = 'id'; // default
    if ($columnsRes) {
        while ($col = mysqli_fetch_assoc($columnsRes)) {
            if ($col['Field'] == 'roadmap_id') {
                $idColumn = 'roadmap_id';
                break;
            }
        }
    }
    
    // Delete steps first (if table exists)
    $tableCheck = mysqli_query($conn, "SHOW TABLES LIKE 'user_roadmap_steps'");
    if ($tableCheck && mysqli_num_rows($tableCheck) > 0) {
        $stepsResult = mysqli_query($conn, "DELETE FROM user_roadmap_steps WHERE roadmap_id = $roadmap_id");
        error_log("Deleted user_roadmap_steps: " . mysqli_affected_rows($conn));
    }
    
    // Delete the roadmap
    $result = mysqli_query($conn, "DELETE FROM user_roadmaps WHERE $idColumn = $roadmap_id AND user_id = $user_id");
    
    if ($result && mysqli_affected_rows($conn) > 0) {
        $deleted = true;
        error_log("Deleted user roadmap successfully");
    } else {
        error_log("Failed to delete user roadmap: " . mysqli_error($conn));
    }
    
} else {
    // Delete from roadmaps table (SYSTEM roadmaps)
    
    // Delete steps first (if table exists)
    $tableCheck = mysqli_query($conn, "SHOW TABLES LIKE 'roadmap_steps'");
    if ($tableCheck && mysqli_num_rows($tableCheck) > 0) {
        $stepsResult = mysqli_query($conn, "DELETE FROM roadmap_steps WHERE roadmap_id = $roadmap_id");
        error_log("Deleted roadmap_steps: " . mysqli_affected_rows($conn));
    }
    
    // Delete the roadmap
    $result = mysqli_query($conn, "DELETE FROM roadmaps WHERE roadmap_id = $roadmap_id AND user_id = $user_id");
    
    if ($result && mysqli_affected_rows($conn) > 0) {
        $deleted = true;
        error_log("Deleted system roadmap successfully");
    } else {
        error_log("Failed to delete system roadmap: " . mysqli_error($conn));
    }
}

$conn->close();

if ($deleted) {
    success(['message' => 'Roadmap deleted successfully']);
} else {
    error('Roadmap not found or you are not authorized to delete it');
}
?>
