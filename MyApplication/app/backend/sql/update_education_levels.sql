-- Education Levels Database Update Script
-- Run this in phpMyAdmin to add/update education levels

-- OPTION 1: If you need to update existing education_levels table
-- Check current data first: SELECT * FROM education_levels;

-- Clear and reinsert all education levels (backup your data first!)
-- DELETE FROM education_levels;

-- Insert/Update education levels with correct IDs
INSERT INTO education_levels (education_level_id, level_name, description) VALUES
(1, '10th Pass', 'Secondary School Certificate (SSC)'),
(2, '12th Science', 'Higher Secondary - Science Stream (PCM/PCB)'),
(3, '12th Commerce', 'Higher Secondary - Commerce Stream'),
(4, '12th Arts', 'Higher Secondary - Arts/Humanities Stream'),
(5, 'Diploma', 'Polytechnic/Technical Diploma'),
(6, 'Undergraduate', 'Bachelor''s Degree (B.Tech, BBA, BA, B.Com, etc.)'),
(7, 'Postgraduate', 'Master''s Degree (M.Tech, MBA, MA, M.Com, etc.)')
ON DUPLICATE KEY UPDATE 
    level_name = VALUES(level_name),
    description = VALUES(description);

-- If the above doesn't work, use this alternative (run each separately):
-- UPDATE education_levels SET level_name = '10th Pass', description = 'Secondary School Certificate (SSC)' WHERE education_level_id = 1;
-- UPDATE education_levels SET level_name = '12th Science', description = 'Higher Secondary - Science Stream (PCM/PCB)' WHERE education_level_id = 2;
-- UPDATE education_levels SET level_name = '12th Commerce', description = 'Higher Secondary - Commerce Stream' WHERE education_level_id = 3;
-- INSERT INTO education_levels (education_level_id, level_name, description) VALUES (4, '12th Arts', 'Higher Secondary - Arts/Humanities Stream');
-- You may need to update IDs 4,5,6 to 5,6,7 first if they exist

-- STREAMS for 12th Arts (education_level_id = 4)
-- Add streams for 12th Arts
INSERT INTO streams (stream_name, education_level_id, description, difficulty_level, duration) VALUES
('B.A. (Bachelor of Arts)', 4, 'Arts undergraduate degree', 'Medium', '3 years'),
('B.A. Psychology', 4, 'Psychology specialization', 'Medium', '3 years'),
('B.A. English', 4, 'English Literature', 'Medium', '3 years'),
('B.A. History', 4, 'History specialization', 'Medium', '3 years'),
('B.A. Political Science', 4, 'Political Science', 'Medium', '3 years'),
('B.A. Sociology', 4, 'Sociology specialization', 'Medium', '3 years'),
('B.Des (Design)', 4, 'Design specialization', 'Medium', '4 years'),
('BFA (Fine Arts)', 4, 'Fine Arts degree', 'Medium', '4 years'),
('BJMC (Journalism)', 4, 'Mass Communication', 'Medium', '3 years'),
('LLB (Law)', 4, 'Law degree', 'High', '5 years')
ON DUPLICATE KEY UPDATE stream_name = stream_name;

-- Verify the data
-- SELECT * FROM education_levels ORDER BY education_level_id;
-- SELECT * FROM streams WHERE education_level_id = 4;
