-- User Roadmaps Feature Database Schema
-- Run this SQL in phpMyAdmin to create required tables

-- Table to store user-generated roadmaps
CREATE TABLE IF NOT EXISTS user_roadmaps (
    roadmap_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) DEFAULT 'My Career Roadmap',
    target_job_name VARCHAR(255),
    target_salary VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table to store individual steps in each roadmap
CREATE TABLE IF NOT EXISTS user_roadmap_steps (
    step_id INT AUTO_INCREMENT PRIMARY KEY,
    roadmap_id INT NOT NULL,
    step_order INT NOT NULL,
    step_type ENUM('EDUCATION_LEVEL', 'STREAM', 'EXAM', 'EXPERIENCE', 'JOB_PREP', 'JOB') NOT NULL,
    reference_id INT DEFAULT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    icon VARCHAR(50) DEFAULT 'ic_stream',
    FOREIGN KEY (roadmap_id) REFERENCES user_roadmaps(roadmap_id) ON DELETE CASCADE,
    INDEX idx_roadmap_order (roadmap_id, step_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
