<?php
/**
 * save_user_roadmap.php - Save user-created roadmap
 * Creates roadmap entry and steps in database
 * Compatible with existing user_roadmaps table schema
 */
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

include("config/db.php");

// Get JSON input
$data = json_decode(file_get_contents("php://input"), true);

// Debug log
error_log("save_user_roadmap.php received: " . json_encode($data));

$user_id = intval($data['user_id'] ?? 0);
$title = trim($data['title'] ?? 'My Roadmap');
$target_job_name = trim($data['target_job_name'] ?? '');
$target_salary = trim($data['target_salary'] ?? '');
$steps = $data['steps'] ?? [];

if ($user_id <= 0) {
    echo json_encode([
        "status" => false,
        "success" => false,
        "message" => "Invalid user_id"
    ]);
    exit;
}

if (empty($steps)) {
    echo json_encode([
        "status" => false,
        "success" => false,
        "message" => "No steps provided"
    ]);
    exit;
}

// Check if user_roadmaps table exists
$tableCheck = mysqli_query($conn, "SHOW TABLES LIKE 'user_roadmaps'");
$tableExists = mysqli_num_rows($tableCheck) > 0;

// Detect primary key column name if table exists
$useRoadmapId = true; // Default to schema standard
if ($tableExists) {
    $columnsRes = mysqli_query($conn, "DESCRIBE user_roadmaps");
    $useRoadmapId = false;
    while ($col = mysqli_fetch_assoc($columnsRes)) {
        if ($col['Field'] == 'roadmap_id') {
            $useRoadmapId = true;
            break;
        }
    }
}

if (!$tableExists) {
    // Create table matching the schema file (roadmap_id as primary key)
    mysqli_query($conn, "
        CREATE TABLE user_roadmaps (
            roadmap_id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            title VARCHAR(255) DEFAULT 'My Career Roadmap',
            target_job_name VARCHAR(255),
            target_salary VARCHAR(100),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            INDEX idx_user_id (user_id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    ");
    $useRoadmapId = true;
}

// Check if user_roadmap_steps table exists
$tableCheck2 = mysqli_query($conn, "SHOW TABLES LIKE 'user_roadmap_steps'");
if (mysqli_num_rows($tableCheck2) == 0) {
    mysqli_query($conn, "
        CREATE TABLE user_roadmap_steps (
            step_id INT AUTO_INCREMENT PRIMARY KEY,
            roadmap_id INT NOT NULL,
            step_order INT NOT NULL,
            step_type VARCHAR(50) NOT NULL,
            reference_id INT DEFAULT NULL,
            title VARCHAR(255) NOT NULL,
            description TEXT,
            icon VARCHAR(50) DEFAULT 'ic_stream',
            INDEX idx_roadmap_order (roadmap_id, step_order)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    ");
}

// Start transaction
mysqli_begin_transaction($conn);

try {
    // Escape strings
    $title_esc = mysqli_real_escape_string($conn, $title);
    $job_esc = mysqli_real_escape_string($conn, $target_job_name);
    $salary_esc = mysqli_real_escape_string($conn, $target_salary);
    
    // Insert roadmap
    $result = mysqli_query($conn, "
        INSERT INTO user_roadmaps (user_id, title, target_job_name, target_salary) 
        VALUES ($user_id, '$title_esc', '$job_esc', '$salary_esc')
    ");
    
    if (!$result) {
        throw new Exception("Failed to create roadmap: " . mysqli_error($conn));
    }
    
    $roadmap_id = mysqli_insert_id($conn);
    error_log("Created user roadmap with ID: " . $roadmap_id);
    
    // Insert steps
    foreach ($steps as $index => $step) {
        $step_order = $index + 1;
        $step_type = mysqli_real_escape_string($conn, $step['step_type'] ?? 'STEP');
        $reference_id = intval($step['reference_id'] ?? 0);
        $step_title = mysqli_real_escape_string($conn, $step['title'] ?? '');
        $step_desc = mysqli_real_escape_string($conn, $step['description'] ?? '');
        $step_icon = mysqli_real_escape_string($conn, $step['icon'] ?? 'ic_step');
        
        $stepResult = mysqli_query($conn, "
            INSERT INTO user_roadmap_steps 
            (roadmap_id, step_order, step_type, reference_id, title, description, icon) 
            VALUES ($roadmap_id, $step_order, '$step_type', $reference_id, '$step_title', '$step_desc', '$step_icon')
        ");
        
        if (!$stepResult) {
            throw new Exception("Failed to insert step: " . mysqli_error($conn));
        }
    }
    
    // Commit
    mysqli_commit($conn);
    
    error_log("Roadmap saved successfully with " . count($steps) . " steps");
    
    // Return both status and success for compatibility
    echo json_encode([
        "status" => true,
        "success" => true,
        "data" => [
            "roadmap_id" => $roadmap_id,
            "steps_count" => count($steps)
        ],
        "message" => "Roadmap saved successfully"
    ]);
    
} catch (Exception $e) {
    mysqli_rollback($conn);
    error_log("Error in save_user_roadmap: " . $e->getMessage());
    
    echo json_encode([
        "status" => false,
        "success" => false,
        "message" => $e->getMessage()
    ]);
}
?>
