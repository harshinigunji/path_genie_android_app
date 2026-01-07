-- Users Table Update Script
-- Run this in phpMyAdmin to add new profile columns

-- Add new columns to users table (if they don't exist)
ALTER TABLE users
ADD COLUMN IF NOT EXISTS phone VARCHAR(20) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS date_of_birth DATE DEFAULT NULL,
ADD COLUMN IF NOT EXISTS education_level VARCHAR(100) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS current_school VARCHAR(255) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS board VARCHAR(100) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS last_exam_score VARCHAR(50) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS aspiring_career VARCHAR(255) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS profile_image VARCHAR(500) DEFAULT NULL;

-- Alternative: Run each separately if the above doesn't work
-- ALTER TABLE users ADD COLUMN phone VARCHAR(20) DEFAULT NULL;
-- ALTER TABLE users ADD COLUMN date_of_birth DATE DEFAULT NULL;
-- ALTER TABLE users ADD COLUMN education_level VARCHAR(100) DEFAULT NULL;
-- ALTER TABLE users ADD COLUMN current_school VARCHAR(255) DEFAULT NULL;
-- ALTER TABLE users ADD COLUMN board VARCHAR(100) DEFAULT NULL;
-- ALTER TABLE users ADD COLUMN last_exam_score VARCHAR(50) DEFAULT NULL;
-- ALTER TABLE users ADD COLUMN aspiring_career VARCHAR(255) DEFAULT NULL;
-- ALTER TABLE users ADD COLUMN profile_image VARCHAR(500) DEFAULT NULL;

-- Verify the changes
-- DESCRIBE users;
