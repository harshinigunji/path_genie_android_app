<?php
/**
 * save_system_roadmap.php - Save system-generated roadmap
 * Uses the existing roadmaps and roadmap_steps tables
 */
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

include("config/db.php");

// Get JSON input
$json = file_get_contents("php://input");
$data = json_decode($json, true);

// Validate input
$user_id = intval($data['user_id'] ?? 0);
$target_job_name = trim($data['target_job_name'] ?? '');
$target_job_id = intval($data['target_job_id'] ?? 0);
$stream_id = intval($data['stream_id'] ?? 0);
$steps = $data['steps'] ?? [];

if ($user_id <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "Invalid user_id"
    ]);
    exit;
}

if (empty($steps)) {
    echo json_encode([
        "success" => false,
        "message" => "No steps provided"
    ]);
    exit;
}

// Start transaction
mysqli_begin_transaction($conn);

try {
    // Insert into roadmaps table (using existing schema)
    // roadmaps table has: roadmap_id, user_id, roadmap_type, start_stream_id, target_job_id, created_at
    $result = mysqli_query($conn, "
        INSERT INTO roadmaps (user_id, roadmap_type, start_stream_id, target_job_id, created_at) 
        VALUES ($user_id, 'SYSTEM', $stream_id, $target_job_id, NOW())
    ");
    
    if (!$result) {
        throw new Exception("Failed to create roadmap: " . mysqli_error($conn));
    }
    
    $roadmap_id = mysqli_insert_id($conn);
    
    // Insert steps into roadmap_steps table
    // roadmap_steps table has: step_id, roadmap_id, step_order, step_type, title, description, icon
    foreach ($steps as $index => $step) {
        $step_order = $index + 1;
        $step_type = mysqli_real_escape_string($conn, $step['step_type'] ?? 'STEP');
        $step_title = mysqli_real_escape_string($conn, $step['title'] ?? '');
        $step_desc = mysqli_real_escape_string($conn, $step['description'] ?? '');
        $step_icon = mysqli_real_escape_string($conn, $step['icon'] ?? 'ic_step');
        
        $stepResult = mysqli_query($conn, "
            INSERT INTO roadmap_steps (roadmap_id, step_order, step_type, title, description, icon) 
            VALUES ($roadmap_id, $step_order, '$step_type', '$step_title', '$step_desc', '$step_icon')
        ");
        
        if (!$stepResult) {
            throw new Exception("Failed to insert step: " . mysqli_error($conn));
        }
    }
    
    // Commit
    mysqli_commit($conn);
    
    echo json_encode([
        "success" => true,
        "roadmap_id" => $roadmap_id,
        "message" => "Roadmap saved successfully",
        "steps_count" => count($steps)
    ]);
    
} catch (Exception $e) {
    mysqli_rollback($conn);
    
    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}
?>
