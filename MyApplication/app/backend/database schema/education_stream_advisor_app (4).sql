-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Jan 05, 2026 at 09:25 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `education_stream_advisor_app`
--

-- --------------------------------------------------------

--
-- Table structure for table `ai_chat_history`
--

CREATE TABLE `ai_chat_history` (
  `chat_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_message` text DEFAULT NULL,
  `ai_reply` text DEFAULT NULL,
  `education_level_id` int(11) DEFAULT NULL,
  `stream_id` int(11) DEFAULT NULL,
  `roadmap_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `education_levels`
--

CREATE TABLE `education_levels` (
  `education_level_id` int(11) NOT NULL,
  `level_name` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `education_levels`
--

INSERT INTO `education_levels` (`education_level_id`, `level_name`, `description`) VALUES
(1, '10th Pass', 'Students who have completed 10th standard and are choosing their next academic or vocational path'),
(2, '12th Science', 'Students who completed 12th with Science stream (PCM / PCB / PCMB)'),
(3, '12th Commerce', 'Students who completed 12th with Commerce stream'),
(4, '12th Arts', 'Students who completed 12th with Arts / Humanities'),
(5, 'Diploma', 'Students who completed a diploma course after 10th or 12th'),
(6, 'Undergraduate', 'Students who completed a bachelor’s degree'),
(7, 'Postgraduate', 'Students who completed a master’s degree');

-- --------------------------------------------------------

--
-- Table structure for table `education_level_exams`
--

CREATE TABLE `education_level_exams` (
  `id` int(11) NOT NULL,
  `education_level_id` int(11) NOT NULL,
  `exam_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `education_level_exams`
--

INSERT INTO `education_level_exams` (`id`, `education_level_id`, `exam_id`) VALUES
(32, 1, 1),
(33, 1, 2),
(34, 1, 3),
(35, 2, 4),
(36, 2, 5),
(37, 2, 6),
(38, 2, 7),
(39, 2, 8),
(40, 2, 9),
(41, 2, 22),
(47, 3, 9),
(42, 3, 10),
(43, 3, 11),
(44, 3, 18),
(45, 3, 19),
(46, 3, 20),
(48, 4, 12),
(49, 4, 13),
(50, 4, 21),
(63, 4, 33),
(51, 5, 14),
(52, 6, 15),
(53, 6, 16),
(54, 6, 17),
(55, 6, 23),
(56, 6, 24),
(57, 6, 25),
(58, 6, 34),
(59, 7, 26),
(60, 7, 27),
(61, 7, 28),
(62, 7, 29);

-- --------------------------------------------------------

--
-- Table structure for table `education_level_jobs`
--

CREATE TABLE `education_level_jobs` (
  `id` int(11) NOT NULL,
  `education_level_id` int(11) NOT NULL,
  `job_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `education_level_jobs`
--

INSERT INTO `education_level_jobs` (`id`, `education_level_id`, `job_id`) VALUES
(65, 1, 53),
(66, 1, 54),
(67, 1, 55),
(68, 1, 56),
(69, 1, 57),
(70, 1, 58),
(71, 1, 59),
(72, 1, 60),
(75, 2, 61),
(76, 2, 62),
(77, 2, 63),
(73, 2, 64),
(74, 2, 65),
(78, 3, 61),
(79, 3, 62),
(80, 3, 63),
(81, 3, 66),
(82, 4, 61),
(83, 4, 63),
(84, 4, 67),
(85, 4, 69),
(86, 5, 42),
(87, 5, 43),
(88, 5, 60),
(89, 6, 1),
(90, 6, 2),
(91, 6, 3),
(92, 6, 4),
(93, 6, 7),
(94, 6, 8),
(95, 6, 10),
(96, 6, 11),
(97, 6, 12),
(98, 6, 24),
(99, 6, 25),
(100, 6, 30),
(101, 6, 31),
(102, 6, 32),
(103, 6, 33),
(104, 6, 34),
(105, 6, 36),
(106, 6, 37),
(107, 6, 38),
(108, 6, 39),
(109, 6, 40),
(110, 6, 41),
(111, 6, 44),
(112, 6, 45),
(113, 6, 46),
(114, 6, 47),
(115, 6, 48),
(116, 7, 13),
(117, 7, 14),
(118, 7, 23),
(119, 7, 45),
(124, 7, 46),
(125, 7, 48),
(120, 7, 49),
(121, 7, 50),
(122, 7, 51),
(123, 7, 52);

-- --------------------------------------------------------

--
-- Table structure for table `entrance_exams`
--

CREATE TABLE `entrance_exams` (
  `exam_id` int(11) NOT NULL,
  `exam_name` varchar(100) DEFAULT NULL,
  `conducting_body` varchar(100) DEFAULT NULL,
  `exam_stage` enum('After 10th','After 12th-Science','After 12th-Commerce','After 12th-Arts','After Diploma','After UG','After PG') NOT NULL,
  `overview` text DEFAULT NULL,
  `eligibility` text DEFAULT NULL,
  `exam_pattern` text DEFAULT NULL,
  `application_period` varchar(100) DEFAULT NULL,
  `outcome` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `entrance_exams`
--

INSERT INTO `entrance_exams` (`exam_id`, `exam_name`, `conducting_body`, `exam_stage`, `overview`, `eligibility`, `exam_pattern`, `application_period`, `outcome`) VALUES
(1, 'Polytechnic Common Entrance Test (POLYCET)', 'State Technical Education Boards', 'After 10th', 'Entrance exam for admission into diploma courses in engineering and non-engineering fields.', 'Passed 10th standard with Mathematics as a subject.', 'Objective-type questions from Mathematics, Physics, and Chemistry.', 'April – May', 'Admission into Polytechnic and Diploma courses.'),
(2, 'ITI Entrance Test', 'State Skill Development / ITI Boards', 'After 10th', 'Entrance test for admission into Industrial Training Institute (ITI) trade courses.', 'Passed 8th or 10th standard (varies by trade).', 'Basic aptitude, trade-related and general knowledge questions.', 'May – June', 'Admission into ITI trade courses.'),
(3, 'Sainik School Entrance Exam (AISSEE)', 'National Testing Agency (NTA)', 'After 10th', 'National-level entrance exam for admission into Sainik Schools for Class 11.', 'Passed 10th standard and age criteria as prescribed.', 'Objective-type questions on Mathematics, English, and Intelligence.', 'September – October', 'Admission into Sainik Schools (Defense-oriented education).'),
(4, 'JEE Main', 'National Testing Agency (NTA)', 'After 12th-Science', 'National-level engineering entrance exam for admission to NITs, IIITs, and other engineering colleges.', 'Passed 12th with Physics, Chemistry, and Mathematics.', 'Computer-based test with Physics, Chemistry, and Mathematics questions.', 'January – April', 'Eligibility for admission into B.Tech / BE programs.'),
(5, 'JEE Advanced', 'IITs (Joint Admission Board)', 'After 12th-Science', 'Advanced-level engineering entrance exam for admission to IITs.', 'Qualified JEE Main and top rank holders.', 'Computer-based test with advanced problem-solving questions.', 'May – June', 'Admission into IITs.'),
(6, 'NEET-UG', 'National Testing Agency (NTA)', 'After 12th-Science', 'National medical entrance exam for MBBS, BDS, and AYUSH courses.', 'Passed 12th with Physics, Chemistry, and Biology.', 'Offline exam with Biology, Physics, and Chemistry questions.', 'May – June', 'Admission into medical and dental colleges.'),
(7, 'BITSAT', 'Birla Institute of Technology & Science', 'After 12th-Science', 'Entrance exam for admission into BITS Pilani campuses.', 'Passed 12th with PCM.', 'Online test with PCM, English, and Logical Reasoning.', 'May – June', 'Admission into BITS engineering programs.'),
(8, 'NATA', 'Council of Architecture', 'After 12th-Science', 'National Aptitude Test in Architecture for B.Arch admissions.', 'Passed 12th with Mathematics.', 'Aptitude test with drawing and mathematics questions.', 'April – July', 'Eligibility for Architecture admissions.'),
(9, 'CUET-UG-Science', 'National Testing Agency (NTA)', 'After 12th-Science', 'Common entrance test for central universities (science programs).', '12th Science from a recognized board.', 'Subject-based MCQs.', 'February – March', 'Admission into B.Sc and integrated science programs.'),
(10, 'IPMAT', 'IIM Indore', 'After 12th-Science', 'Integrated Program in Management Aptitude Test.', 'Passed 12th with Mathematics.', 'Quantitative Aptitude, Verbal Ability, Logical Reasoning.', 'May – June', 'Admission into 5-year integrated management programs.'),
(11, 'CA Foundation', 'Institute of Chartered Accountants of India (ICAI)', 'After 12th-Commerce', 'Entry-level exam for Chartered Accountancy.', 'Passed or appeared in 12th standard.', 'Accounting, Law, Economics, Quantitative Aptitude.', 'June & December', 'Eligibility to pursue Chartered Accountancy.'),
(12, 'CLAT UG', 'Consortium of National Law Universities', 'After 12th-Arts', 'National-level law entrance exam for NLUs.', '12th pass from any stream.', 'English, Legal Reasoning, GK, Logical Reasoning.', 'July – August', 'Admission into BA LLB and integrated law programs.'),
(13, 'CUET- UG- Arts', 'National Testing Agency (NTA)', 'After 12th-Arts', 'Entrance exam for Arts and Humanities undergraduate programs.', '12th Arts from a recognized board.', 'Subject-based computer test.', 'May – June', 'Admission into BA and humanities programs.'),
(14, 'Lateral Entry Entrance Test (LEET)', 'State Technical Education Boards', 'After Diploma', 'Entrance exam for diploma holders to join B.Tech directly in 2nd year.', 'Completed diploma in engineering.', 'Technical subject-based test.', 'June – July', 'Admission into B.Tech (Lateral Entry).'),
(15, 'GATE', 'IITs', 'After UG', 'National-level exam for postgraduate engineering admissions.', 'Completed B.Tech or equivalent.', 'Engineering subject-based test.', 'February', 'Admission into M.Tech and PSU jobs.'),
(16, 'CAT', 'Indian Institutes of Management', 'After UG', 'Management entrance exam for MBA programs.', 'Completed any undergraduate degree.', 'Quantitative, Verbal, Logical Reasoning.', 'November', 'Admission into MBA programs.'),
(17, 'NET-UG', 'UGC / NTA', 'After UG', 'Eligibility test for assistant professor and PhD.', 'Postgraduate degree.', 'Subject-based test.', 'June & December', 'Eligibility for teaching and PhD.'),
(18, 'Company Secretary Executive Entrance Test (CSEET)', 'Institute of Company Secretaries of India (ICSI)', 'After 12th-Commerce', 'National-level entrance exam for admission into the Company Secretary course.', 'Passed 12th standard in any stream except Fine Arts.', 'Computer-based test with MCQs covering Business Communication, Legal Aptitude, Economics, and Current Affairs.', 'Multiple sessions every year', 'Eligibility to enroll in CS Executive Programme.'),
(19, 'CMA Foundation', 'Institute of Cost Accountants of India (ICMAI)', 'After 12th-Commerce', 'Entry-level examination for the Cost and Management Accountancy course.', 'Passed 12th standard (Commerce preferred).', 'Objective-type papers covering Accounting, Economics, Mathematics, and Laws.', 'January – July', 'Eligibility to proceed to CMA Intermediate level.'),
(20, 'NMIMS (NPAT)', 'NMIMS University', 'After 12th-Commerce', 'Entrance exam for undergraduate management and commerce programs.', '12th Commerce from a recognized board.', 'MCQs on Quantitative Ability, Reasoning, and English.', 'March – May', 'Admission into BBA and commerce programs at NMIMS.'),
(21, '(NCHMCT JEE)-National Council for Hotel Management Joint Entrance Examination', 'National Testing Agency (NTA) on behalf of NCHMCT', 'After 12th-Arts', 'National-level entrance exam for admission into hotel management and hospitality programs.', 'Passed 12th standard with English as a compulsory subject.', 'Computer-based test with MCQs covering English, Reasoning, GK, and Aptitude.', 'February – April', 'Admission into BHM and hospitality programs under NCHMCT.'),
(22, 'State Common Entrance Tests (State CETs)', 'State Examination Authorities', 'After 12th-Science', 'State-level entrance examinations conducted by individual states for admission into undergraduate degree and professional courses.', 'Passed 12th standard from a recognized board; subject requirements vary by course and state.', 'Objective-type questions covering Mathematics, Science, Aptitude, and subject-specific syllabus depending on the course.', 'April – June (varies by state)', 'Admission into state-level undergraduate programs such as BCA, B.Sc (Computer Science/IT), BBA, and related courses.'),
(23, 'Common University Entrance Test – Postgraduate (CUET-PG)', 'National Testing Agency (NTA)', 'After UG', 'National-level entrance exam for admission into postgraduate programs offered by central and participating universities.', 'Bachelor’s degree in a relevant discipline from a recognized university.', 'Computer-based test with subject-specific multiple-choice questions.', 'March – April', 'Admission into MA, M.Com, and other PG programs in central and participating universities.'),
(24, 'State PG Common Entrance Tests (State PG CETs)', 'State Examination Authorities', 'After UG', 'State-level entrance exams conducted for admission into postgraduate courses in state universities and colleges.', 'Bachelor’s degree in a relevant discipline; eligibility criteria vary by state.', 'Objective-type questions based on subject knowledge and aptitude.', 'May – July (varies by state)', 'Admission into MA, M.Com, and other PG programs in state universities.'),
(25, 'Common Law Admission Test – PG (CLAT-PG)', 'Consortium of National Law Universities', 'After UG', 'National-level entrance exam for admission into LLM programs offered by National Law Universities.', 'LLB degree or equivalent from a recognized university.', 'Computer-based test with objective questions on constitutional law, jurisprudence, and other law subjects.', 'April – May', 'Admission into LLM programs at National Law Universities.'),
(26, 'UGC National Eligibility Test (UGC NET)', 'National Testing Agency (NTA)', 'After PG', 'National-level qualifying examination for eligibility as Assistant Professor and Junior Research Fellowship (JRF).', 'Postgraduate degree with minimum required percentage (as per UGC norms).', 'Computer-based test with two papers: subject knowledge and research aptitude.', 'March – April / September – October', 'Eligibility for Assistant Professor and award of Junior Research Fellowship.'),
(27, 'CSIR National Eligibility Test (CSIR NET)', 'Council of Scientific & Industrial Research (CSIR)', 'After PG', 'National-level test for determining eligibility for JRF and lectureship in science subjects.', 'Postgraduate degree in science subjects with required percentage.', 'Computer-based test with subject-specific and research aptitude questions.', 'June – July / December – January', 'Eligibility for JRF and Assistant Professor in science disciplines.'),
(28, 'State Eligibility Test (SET)', 'State Examination Authorities', 'After PG', 'State-level eligibility examination for Assistant Professor positions in universities and colleges.', 'Postgraduate degree with required percentage as prescribed by the state.', 'Objective-type questions based on subject knowledge and teaching aptitude.', 'Varies by state', 'Eligibility for Assistant Professor within the respective state.'),
(29, 'University-level PhD / Professional Doctorate Entrance Test', 'Universities / Higher Education Institutions', 'After PG', 'Entrance examination and selection process for admission into PhD, DBA, and Doctor of Laws programs.', 'Postgraduate degree (and LLM for Law doctorates) with research eligibility.', 'Written test on research aptitude followed by interview and proposal presentation.', 'Varies by university', 'Admission into PhD / DBA / Professional Doctorate programs.'),
(31, 'CUET-UG-Commerce', 'National Testing Agency (NTA)', 'After 12th-Commerce', 'Entrance exam for commerce and management programs in central universities.', '12th Commerce from a recognized board.', 'Subject-based MCQs.', 'February – March', 'Admission into B.Com, BBA, and related programs.'),
(33, 'NIFT Entrance Exam', 'NIFT', 'After 12th-Arts', 'Entrance exam for fashion and design programs', '12th pass from any stream', 'Creative aptitude + design test', 'October – January', 'Admission into NIFT programs'),
(34, 'XAT', 'XLRI', 'After UG', 'MBA entrance exam for XLRI and partner institutes', 'Bachelor degree', 'Aptitude + Decision Making', 'August – November', 'Admission into MBA programs');

-- --------------------------------------------------------

--
-- Table structure for table `forum_answers`
--

CREATE TABLE `forum_answers` (
  `answer_id` int(11) NOT NULL,
  `question_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `answer_text` text NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `forum_answers`
--

INSERT INTO `forum_answers` (`answer_id`, `question_id`, `user_id`, `answer_text`, `created_at`) VALUES
(1, 1, 2, 'Yes, BCA is good if you are interested in programming.', '2025-12-31 00:40:09'),
(2, 1, 7, 'Yes, You can go for it', '2025-12-31 12:42:38'),
(3, 3, 7, 'Commerce', '2025-12-31 12:46:44'),
(4, 3, 6, 'You can go for Commerce', '2025-12-31 13:32:01'),
(5, 2, 6, 'B.Com', '2025-12-31 13:33:00'),
(6, 4, 7, 'Please specify you domain.', '2025-12-31 13:36:11'),
(7, 5, 9, 'Yes', '2026-01-02 18:48:46');

-- --------------------------------------------------------

--
-- Table structure for table `forum_answer_likes`
--

CREATE TABLE `forum_answer_likes` (
  `id` int(11) NOT NULL,
  `answer_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `forum_likes`
--

CREATE TABLE `forum_likes` (
  `like_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `question_id` int(11) DEFAULT NULL,
  `answer_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `forum_likes`
--

INSERT INTO `forum_likes` (`like_id`, `user_id`, `question_id`, `answer_id`, `created_at`) VALUES
(6, 6, 3, NULL, '2025-12-31 08:02:09'),
(7, 6, 2, NULL, '2025-12-31 08:02:29'),
(8, 7, 4, NULL, '2025-12-31 08:05:20'),
(11, 6, 1, NULL, '2025-12-31 08:21:21'),
(28, 9, 1, NULL, '2026-01-03 10:40:14'),
(29, 9, 3, NULL, '2026-01-03 10:40:16');

-- --------------------------------------------------------

--
-- Table structure for table `forum_questions`
--

CREATE TABLE `forum_questions` (
  `question_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `education_level_id` int(11) NOT NULL,
  `status` enum('OPEN','ANSWERED') DEFAULT 'OPEN',
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `forum_questions`
--

INSERT INTO `forum_questions` (`question_id`, `user_id`, `title`, `description`, `education_level_id`, `status`, `created_at`) VALUES
(1, 1, 'Is BCA good after 12th?', 'I am confused between BCA and BSc CS.', 2, 'ANSWERED', '2025-12-31 00:31:06'),
(2, 7, 'What is the best option after 12th Commerce?', 'What streams are best after 12th Commerce students', 2, 'ANSWERED', '2025-12-31 12:43:59'),
(3, 7, 'Which stream is best after 10th to become Lawyer', 'Pathway to lawyer', 1, 'ANSWERED', '2025-12-31 12:46:10'),
(4, 6, 'Suggest jobs after Diploma', 'Jobs related to Diploma', 3, 'ANSWERED', '2025-12-31 13:33:40'),
(5, 13, 'Is PCM good after 10th?', 'Is PCM good after the 10th standard?', 1, 'ANSWERED', '2026-01-02 18:47:56');

-- --------------------------------------------------------

--
-- Table structure for table `forum_replies`
--

CREATE TABLE `forum_replies` (
  `reply_id` int(11) NOT NULL,
  `answer_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `reply_text` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `jobs`
--

CREATE TABLE `jobs` (
  `job_id` int(11) NOT NULL,
  `job_name` varchar(100) DEFAULT NULL,
  `job_type` enum('Government','Private') DEFAULT NULL,
  `description` text DEFAULT NULL,
  `required_education` text DEFAULT NULL,
  `required_exams` text DEFAULT NULL,
  `career_growth` text DEFAULT NULL,
  `average_salary` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `jobs`
--

INSERT INTO `jobs` (`job_id`, `job_name`, `job_type`, `description`, `required_education`, `required_exams`, `career_growth`, `average_salary`) VALUES
(1, 'Software Engineer', 'Private', 'Designs, develops, tests, and maintains software applications and systems.', 'B.Tech / B.E in Computer Science or related field', 'Campus Placements, Technical Interviews', 'Junior Engineer → Senior Engineer → Tech Lead → Architect', '6–15 LPA'),
(2, 'Data Analyst', 'Private', 'Analyzes data to help organizations make informed decisions.', 'B.Tech / B.Sc / BCA with data skills', 'Company Interviews, Skill Assessments', 'Data Analyst → Senior Analyst → Data Scientist', '5–12 LPA'),
(3, 'Civil Engineer', 'Government', 'Plans, designs, and supervises construction of infrastructure projects.', 'B.Tech / B.E in Civil Engineering', 'GATE, State Engineering Exams', 'Assistant Engineer → Executive Engineer → Chief Engineer', '6–10 LPA'),
(4, 'Mechanical Engineer', 'Government', 'Works on design, manufacturing, and maintenance of mechanical systems.', 'B.Tech / B.E in Mechanical Engineering', 'GATE, PSU Recruitment Exams', 'Junior Engineer → Senior Engineer → Manager', '6–10 LPA'),
(5, 'Doctor (MBBS)', 'Private', 'Diagnoses and treats illnesses, provides medical care to patients.', 'MBBS Degree', 'NEET', 'Junior Doctor → Specialist → Consultant', '8–20 LPA'),
(6, 'Pharmacist', 'Private', 'Dispenses medicines and advises patients on drug usage.', 'B.Pharm / D.Pharm', 'College Exams, Registration', 'Pharmacist → Senior Pharmacist → Pharma Manager', '4–8 LPA'),
(7, 'Chartered Accountant', 'Private', 'Handles accounting, auditing, taxation, and financial advisory services.', 'CA Qualification', 'CA Foundation, Intermediate, Final', 'Junior CA → Senior CA → Partner', '7–25 LPA'),
(8, 'Bank Probationary Officer', 'Government', 'Manages banking operations and customer services.', 'Any Bachelor’s Degree', 'IBPS PO, SBI PO', 'PO → Branch Manager → Regional Manager', '6–12 LPA'),
(9, 'Business Analyst', 'Private', 'Analyzes business processes and suggests improvements.', 'MBA / BBA / Engineering', 'Company Interviews', 'Analyst → Senior Analyst → Consultant', '6–14 LPA'),
(10, 'Civil Services Officer (IAS/IPS)', 'Government', 'Responsible for administration and governance of public services.', 'Any Bachelor’s Degree', 'UPSC Civil Services Exam', 'Officer → Senior Officer → Secretary', '10–20 LPA'),
(11, 'Lawyer', 'Private', 'Practices law and represents clients in legal matters.', 'LLB Degree', 'CLAT, State Bar Council Exam', 'Junior Lawyer → Senior Advocate → Judge', '5–15 LPA'),
(12, 'Journalist', 'Private', 'Collects, writes, and presents news and information.', 'Degree in Journalism / Mass Communication', 'College Admissions / Skill Assessment', 'Reporter → Senior Journalist → Editor', '4–10 LPA'),
(13, 'Professor', 'Government', 'Teaches and conducts research at universities.', 'PhD', 'NET / University Selection', 'Assistant Professor → Associate Professor → Professor', '8–18 LPA'),
(14, 'Research Scientist', 'Private', 'Conducts scientific research and development.', 'M.Sc / PhD', 'NET, GATE (varies)', 'Scientist → Senior Scientist → Lead Researcher', '7–15 LPA'),
(15, 'Management Trainee', 'Private', 'Entry-level role focused on learning business operations and management processes.', 'BMS / BBA or related management degree', 'Campus Placements, HR & Aptitude Interviews', 'Management Trainee → Assistant Manager → Manager', '4–8 LPA'),
(16, 'Marketing Executive', 'Private', 'Plans and executes marketing strategies to promote products and services.', 'BMS / BBA / Marketing specialization', 'Company Interviews, Marketing Assessments', 'Marketing Executive → Marketing Manager → Brand Manager', '4–10 LPA'),
(17, 'HR Executive', 'Private', 'Handles recruitment, employee relations, and HR operations.', 'BMS / BBA with HR specialization', 'HR Interviews, Aptitude Tests', 'HR Executive → HR Manager → HR Head', '4–9 LPA'),
(18, 'Company Secretary', 'Private', 'Ensures corporate compliance with laws, regulations, and governance standards.', 'Company Secretary (CS) qualification', 'CSEET, CS Executive & Professional Exams', 'Company Secretary → Senior CS → Compliance Head', '8–20 LPA'),
(19, 'Compliance Officer', 'Private', 'Monitors and enforces legal and regulatory compliance within organizations.', 'CS / Law / Commerce background', 'CS Exams, Corporate Interviews', 'Compliance Officer → Compliance Manager → Chief Compliance Officer', '6–15 LPA'),
(20, 'Corporate Legal Advisor', 'Private', 'Advises companies on corporate laws, contracts, and regulatory matters.', 'CS / Law Degree', 'CS Exams, Legal Interviews', 'Legal Advisor → Senior Legal Consultant → Legal Head', '7–18 LPA'),
(21, 'Cost Accountant', 'Private', 'Analyzes and controls costs to improve organizational profitability.', 'CMA qualification', 'CMA Foundation, Intermediate & Final', 'Cost Accountant → Senior Accountant → Finance Controller', '7–18 LPA'),
(22, 'Financial Analyst', 'Private', 'Evaluates financial data to support budgeting, forecasting, and investment decisions.', 'CMA / Commerce / Finance degree', 'CMA Exams, Financial Interviews', 'Financial Analyst → Senior Analyst → Finance Manager', '6–14 LPA'),
(23, 'Finance Manager', 'Private', 'Manages financial planning, budgeting, and financial strategy of an organization.', 'CMA / MBA Finance', 'CMA Final, Leadership Interviews', 'Finance Manager → Senior Manager → CFO', '10–25 LPA'),
(24, 'Administrative Assistant (SSC)', 'Government', 'Provides administrative and clerical support in central government departments.', 'Any Graduate (Commerce / Arts / Science)', 'SSC CHSL / SSC CGL', 'Assistant → Section Officer → Administrative Officer', '3–7 LPA'),
(25, 'State Govt Office Executive', 'Government', 'Handles administrative operations in state government offices.', 'Any Graduate', 'State PSC / State SSC Exams', 'Office Executive → Senior Executive → Officer', '3–8 LPA'),
(26, 'Company Secretary in PSUs', 'Government', 'Manages corporate compliance and governance in public sector undertakings.', 'Company Secretary (CS)', 'CS Professional + PSU Selection Process', 'CS Officer → Senior CS → Board Secretary', '10–22 LPA'),
(27, 'Regulatory Authority Officer', 'Government', 'Works with regulatory bodies ensuring compliance with laws and regulations.', 'CS / Law / Commerce Degree', 'Regulatory Exams / Interviews', 'Officer → Senior Officer → Regulatory Head', '8–18 LPA'),
(28, 'Cost Auditor (Govt Projects)', 'Government', 'Conducts cost audits for government-funded projects and PSUs.', 'CMA Qualification', 'CMA Final + Govt Audit Selection', 'Cost Auditor → Senior Auditor → Audit Director', '9–20 LPA'),
(29, 'Accounts Officer (State / Central Govt)', 'Government', 'Manages accounting and financial records for government departments.', 'Commerce Degree / CMA / CA', 'PSC / SSC / Dept Exams', 'Accounts Officer → Senior Accounts Officer → Finance Controller', '7–16 LPA'),
(30, 'Bank Officer (IBPS / SBI)', 'Government', 'Handles banking operations, customer management, and financial services.', 'Any Graduate', 'IBPS PO / SBI PO', 'Probationary Officer → Branch Manager → Regional Manager', '6–15 LPA'),
(31, 'Public Sector Administrative Roles', 'Government', 'Administrative and managerial roles in public sector undertakings.', 'Any Graduate / Management Degree', 'PSU Exams / Interviews', 'Executive → Manager → Senior Manager', '6–14 LPA'),
(32, 'SSC Officer Posts', 'Government', 'Group B and C officer-level posts under central government departments.', 'Any Graduate', 'SSC CGL', 'Officer → Senior Officer → Gazetted Officer', '6–16 LPA'),
(33, 'Art Teacher (Govt Schools/Colleges)', 'Government', 'Teaches fine arts subjects in government educational institutions.', 'BFA / MFA + B.Ed (where required)', 'TET / NET / State Exams', 'Art Teacher → Senior Teacher → Head of Department', '4–10 LPA'),
(34, 'Cultural Department Artist', 'Government', 'Promotes and preserves cultural heritage through art and performances.', 'BFA / Fine Arts Background', 'State Cultural Dept Selection', 'Artist → Senior Artist → Cultural Officer', '4–9 LPA'),
(35, 'Hotel Manager', 'Private', 'Oversees hotel operations including staff, services, and customer satisfaction.', 'BHM / Hotel Management Degree', 'Institute Placements / Interviews', 'Hotel Manager → General Manager → Regional Manager', '6–15 LPA'),
(36, 'Front Office Executive', 'Private', 'Manages guest reception, reservations, and front desk operations.', 'BHM / Hospitality Degree', 'Institute Placements / Interviews', 'Front Office Executive → Front Office Manager → Hotel Manager', '3–8 LPA'),
(37, 'Food & Beverage Manager', 'Private', 'Manages food services, quality control, and hospitality standards.', 'BHM / Hospitality Management', 'Institute Placements / Interviews', 'F&B Manager → Operations Manager → Hotel Manager', '5–12 LPA'),
(38, 'Hotel Manager in Govt Hotels', 'Government', 'Manages government-owned hotels and hospitality services.', 'BHM / Hotel Management Degree', 'Govt Hospitality Selection', 'Hotel Manager → Senior Manager → Tourism Director', '6–14 LPA'),
(39, 'Catering Officer (Railways / Defence)', 'Government', 'Supervises catering services in railways and defense establishments.', 'BHM / Hospitality Degree', 'Railway / Defence Recruitment Exams', 'Catering Officer → Senior Officer → Catering Director', '6–13 LPA'),
(40, 'Graphic Designer', 'Private', 'Creates visual concepts using design software to communicate ideas that inspire and inform.', 'BFA / Design Degree / Graphic Design Certification', 'Portfolio Review, Design Interviews', 'Graphic Designer → Senior Designer → Art Director', '4–10 LPA'),
(41, 'Visual Artist', 'Private', 'Produces original artworks using various visual art forms such as painting, illustration, and digital media.', 'BFA / Fine Arts Background', 'Portfolio Review, Exhibitions', 'Visual Artist → Senior Artist → Creative Director', '3–9 LPA'),
(42, 'IT Assistant (State & Central Govt)', 'Government', 'Provides technical and IT support services in government departments and offices.', 'BCA / B.Sc (Computer Science/IT) or equivalent', 'State Govt Exams / SSC / Departmental Tests', 'IT Assistant → Senior IT Assistant → IT Officer', '4–9 LPA'),
(43, 'Junior Software Engineer (PSUs)', 'Government', 'Develops and maintains software systems for public sector undertakings.', 'BCA / B.Sc (Computer Science/IT) / Engineering Degree', 'PSU Recruitment Exams / Interviews', 'Junior Engineer → Software Engineer → Senior Engineer', '6–12 LPA'),
(44, 'Bank IT Officer (IBPS / SBI – Specialist Cadre)', 'Government', 'Manages banking IT systems, cybersecurity, and digital banking platforms.', 'BCA / B.Sc (Computer Science/IT) / MCA', 'IBPS SO / SBI SO', 'IT Officer → Senior IT Officer → Chief Technology Officer', '7–15 LPA'),
(45, 'Computer Instructor (Govt Institutions)', 'Government', 'Teaches computer science and IT subjects in government schools and colleges.', 'BCA / B.Sc (Computer Science/IT) + Teaching Qualification', 'TET / NET / State Education Exams', 'Instructor → Senior Instructor → Academic Coordinator', '4–10 LPA'),
(46, 'IT Officer in PSUs', 'Government', 'Oversees IT infrastructure, systems, and digital transformation in PSUs.', 'BCA / B.Sc (Computer Science/IT) / MCA', 'PSU Recruitment Exams / Interviews', 'IT Officer → Senior IT Officer → IT Manager', '8–18 LPA'),
(47, 'Content Writer / Editor', 'Private', 'Creates, edits, and manages written content for digital media, publications, and organizations.', 'MA / Any Arts Degree with strong writing skills', 'Portfolio Review, Writing Tests', 'Content Writer → Senior Editor → Content Manager', '4–10 LPA'),
(48, 'NGO / Development Sector Executive', 'Private', 'Works on social development projects, policy implementation, and community programs.', 'MA / Social Sciences / Relevant Degree', 'Interviews, Project Assessments', 'Project Executive → Program Manager → NGO Director', '4–9 LPA'),
(49, 'Tax Consultant', 'Private', 'Advises individuals and organizations on tax planning, compliance, and filings.', 'M.Com / CA / CMA', 'Professional Exams, Client Interviews', 'Tax Consultant → Senior Consultant → Tax Advisor', '6–15 LPA'),
(50, 'Corporate Analyst', 'Private', 'Analyzes corporate data, financial performance, and business strategies.', 'M.Com / MBA / Finance Background', 'Company Interviews, Analytical Assessments', 'Corporate Analyst → Senior Analyst → Strategy Manager', '6–14 LPA'),
(51, 'Legal Advisor in Govt / PSUs', 'Government', 'Provides legal advice and representation to government departments and public sector undertakings.', 'LLB / LLM', 'Govt Recruitment Exams / Interviews', 'Legal Advisor → Senior Legal Advisor → Legal Head', '8–18 LPA'),
(52, 'Public Prosecutor', 'Government', 'Represents the state in criminal cases and prosecutes offenders in courts.', 'LLB / LLM', 'Judicial / State Prosecution Exams', 'Public Prosecutor → Senior Prosecutor → Director of Prosecution', '7–16 LPA'),
(53, 'Police Constable', 'Government', 'Maintains law and order and performs policing duties at the ground level.', '10th Pass', 'State Police Constable Exam', 'Constable → Head Constable → Sub-Inspector', '3–6 LPA'),
(54, 'Railway Group D', 'Government', 'Performs maintenance and operational support work in Indian Railways.', '10th Pass / ITI', 'Railway Group D Exam', 'Group D Staff → Senior Trackman → Supervisor', '3–5 LPA'),
(55, 'Indian Army – Tradesman / GD', 'Government', 'Carries out trade-specific and general duty roles in the Indian Army.', '10th Pass', 'Army Recruitment Rally / Written Test', 'Soldier → Senior Soldier → JCO', '3–6 LPA'),
(56, 'SSC Multi Tasking Staff (MTS)', 'Government', 'Handles basic office and support duties in central government offices.', '10th Pass', 'SSC MTS Exam', 'MTS → Senior MTS → Office Assistant', '3–5 LPA'),
(57, 'Office Assistant', 'Private', 'Provides administrative and clerical support in offices.', '10th Pass / 12th Preferred', 'Company Interviews', 'Office Assistant → Admin Executive → Office Manager', '2–4 LPA'),
(58, 'Data Entry Operator', 'Private', 'Enters and manages data using computers and basic software.', '10th Pass with Basic Computer Skills', 'Typing Test / Company Interview', 'Data Entry Operator → Senior Operator → Team Lead', '2–4 LPA'),
(59, 'Sales Executive', 'Private', 'Promotes and sells products or services to customers.', '10th Pass / 12th Preferred', 'Company Interview', 'Sales Executive → Senior Executive → Sales Manager', '2–5 LPA'),
(60, 'Helper / Technician', 'Private', 'Assists in technical and maintenance work under supervision.', '10th Pass / ITI', 'Skill Test / Interview', 'Helper → Technician → Senior Technician', '2–4 LPA'),
(61, 'Lower Division Clerk (LDC)', 'Government', 'Performs clerical and administrative work in government departments.', '12th Pass', 'SSC LDC / State LDC Exams', 'LDC → Upper Division Clerk → Section Officer', '4–7 LPA'),
(62, 'Tax Assistant', 'Government', 'Assists in tax assessment, data handling, and departmental operations.', '12th Pass / Commerce Preferred', 'SSC CGL / Departmental Exams', 'Tax Assistant → Senior Assistant → Inspector', '5–8 LPA'),
(63, 'Stenographer', 'Government', 'Handles shorthand, typing, and official documentation in government offices.', '12th Pass', 'SSC Stenographer / State Exams', 'Stenographer → Senior Stenographer → PA', '5–8 LPA'),
(64, 'NDA Officer Cadet', 'Government', 'Undergoes training to become a commissioned officer in the Armed Forces.', '12th Pass (Science for Army/Navy/Air Force)', 'NDA Examination', 'Officer Cadet → Lieutenant → Senior Officer Ranks', '6–12 LPA'),
(65, 'Indian Coast Guard – Navik', 'Government', 'Performs operational and technical duties in the Indian Coast Guard.', '12th Pass (Science with Maths & Physics)', 'Coast Guard Navik Exam', 'Navik → Leading Navik → Petty Officer', '5–9 LPA'),
(66, 'Junior Accountant', 'Private', 'Handles basic accounting, billing, and financial record maintenance.', '12th Commerce / Accounting Background', 'Company Interview / Skill Test', 'Junior Accountant → Accountant → Finance Executive', '3–6 LPA'),
(67, 'Customer Relationship Executive', 'Private', 'Manages customer interactions and resolves service-related queries.', '12th Pass', 'Company Interview', 'CRE → Senior Executive → Team Lead', '3–5 LPA'),
(68, 'Digital Marketing Executive', 'Private', 'Executes online marketing campaigns using social media and digital tools.', '12th Pass / Digital Marketing Certification', 'Skill Assessment / Interview', 'Executive → Digital Marketer → Marketing Manager', '3–7 LPA'),
(69, 'Business Process Executive (BPO)', 'Private', 'Handles voice or non-voice customer support and backend processes.', '12th Pass', 'Company Interview', 'BPO Executive → Senior Executive → Team Leader', '3–5 LPA'),
(70, 'Retail Store Supervisor', 'Private', 'Supervises store operations, staff, and customer service activities.', '12th Pass', 'Interview', 'Supervisor → Store Manager → Area Manager', '3–6 LPA'),
(71, 'Technical Assistant (DRDO/ISRO)', 'Government', 'Assists in technical and laboratory operations', '12th PCM / Diploma', 'DRDO / ISRO Exams', 'Assistant → Senior Assistant → Officer', '4–8 LPA'),
(72, 'Lab Assistant', 'Government', 'Supports laboratory experiments and maintenance', '12th PCM', 'State / Central Exams', 'Lab Assistant → Lab Supervisor', '3–6 LPA'),
(73, 'Junior Technical Assistant', 'Private', 'Supports technical operations in private firms', '12th PCM', 'Interview / Skill Test', 'Assistant → Technician', '3–5 LPA'),
(74, 'Site Supervisor', 'Private', 'Supervises basic construction activities', '12th PCM', 'Interview', 'Supervisor → Site Engineer', '3–6 LPA'),
(75, 'Lab Technician', 'Government', 'Works in medical labs', '12th PCB', 'State Health Exams', 'Technician → Senior Technician', '4–7 LPA'),
(76, 'Health Assistant', 'Government', 'Supports healthcare programs', '12th PCB', 'Health Dept Exams', 'Assistant → Officer', '4–6 LPA'),
(77, 'Medical Lab Assistant', 'Private', 'Handles lab sample processing', '12th PCB', 'Interview', 'Assistant → Lab Manager', '3–5 LPA'),
(78, 'Clinical Data Assistant', 'Private', 'Manages medical data', '12th PCB', 'Interview', 'Assistant → Analyst', '4–6 LPA'),
(79, 'Medical Officer', 'Government', 'Provides treatment in govt hospitals', 'MBBS', 'PSC / Health Exams', 'MO → CMO', '10–20 LPA'),
(80, 'Public Health Officer', 'Government', 'Manages public health programs', 'MBBS', 'PSC / UPSC', 'Officer → Director', '10–18 LPA'),
(81, 'Junior Resident Doctor', 'Private', 'Clinical training role', 'MBBS', 'Hospital Interview', 'JR → Consultant', '8–15 LPA'),
(82, 'Clinical Research Associate', 'Private', 'Manages medical research trials', 'MBBS', 'Interview', 'CRA → Research Manager', '6–12 LPA'),
(83, 'Assistant Engineer', 'Government', 'Engineering role in govt departments', 'B.Tech', 'PSC Exams', 'AE → EE', '8–15 LPA'),
(84, 'Scientist/Engineer', 'Government', 'R&D role in ISRO/DRDO', 'B.Tech', 'ISRO/DRDO Exams', 'Scientist → Senior Scientist', '10–20 LPA'),
(85, 'System Engineer', 'Private', 'Manages system-level engineering tasks', 'B.Tech', 'Interview', 'Engineer → Architect', '6–12 LPA'),
(86, 'Project Engineer', 'Private', 'Handles project execution', 'B.Tech', 'Interview', 'Engineer → Project Manager', '6–14 LPA'),
(87, 'Auditor', 'Government', 'Audits govt departments', 'B.Com', 'PSC / CAG Exams', 'Auditor → Director', '7–14 LPA'),
(88, 'Tax Inspector', 'Government', 'Handles tax assessments', 'B.Com', 'SSC CGL', 'Inspector → Commissioner', '7–15 LPA'),
(89, 'Accounts Executive', 'Private', 'Manages company accounts', 'B.Com', 'Interview', 'Executive → Manager', '4–8 LPA'),
(90, 'Financial Consultant', 'Private', 'Advises clients on finance', 'B.Com', 'Interview', 'Consultant → Partner', '6–12 LPA'),
(91, 'Social Welfare Officer', 'Government', 'Implements social schemes', 'BA', 'PSC Exams', 'Officer → Director', '6–12 LPA'),
(92, 'Archivist', 'Government', 'Manages historical records', 'BA', 'State Exams', 'Archivist → Senior Archivist', '5–9 LPA'),
(93, 'Policy Analyst', 'Private', 'Analyzes public policy', 'BA', 'Interview', 'Analyst → Advisor', '6–12 LPA'),
(94, 'NGO Program Officer', 'Private', 'Manages development programs', 'BA', 'Interview', 'Officer → Program Head', '5–10 LPA'),
(95, 'SSC Auditor', 'Government', 'Conducts audits in government departments under CAG', 'B.Com / CA / CMA', 'SSC CGL', 'Auditor → Senior Auditor → Director', '7–14 LPA'),
(96, 'SSC Tax Assistant', 'Government', 'Handles tax assessment and compliance in Income Tax & Customs', 'B.Com / BBA', 'SSC CGL', 'Tax Assistant → Inspector → Commissioner', '7–15 LPA'),
(97, 'SSC Technical Assistant', 'Government', 'Performs technical duties in government departments', 'B.Sc / BCA / Engineering', 'SSC Technical / CGL', 'Technical Assistant → Technical Officer', '6–12 LPA'),
(98, 'SSC Clerk (LDC/UDC)', 'Government', 'Performs clerical and administrative work', '12th / Any Graduate', 'SSC CHSL / CGL', 'Clerk → Section Officer', '4–7 LPA');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `notification_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `type` varchar(50) NOT NULL COMMENT 'answer, like_question, like_answer',
  `reference_id` int(11) NOT NULL COMMENT 'question_id or answer_id depending on type',
  `from_user_id` int(11) NOT NULL,
  `message` text DEFAULT NULL,
  `is_read` tinyint(4) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`notification_id`, `user_id`, `type`, `reference_id`, `from_user_id`, `message`, `is_read`, `created_at`) VALUES
(1, 1, 'like_question', 1, 7, 'Someone liked your question', 0, '2025-12-31 07:59:27'),
(8, 1, 'like_question', 1, 6, 'Someone liked your question', 0, '2025-12-31 08:21:21'),
(9, 7, 'like_question', 2, 9, 'Someone liked your question', 0, '2026-01-02 03:26:35'),
(10, 7, 'like_answer', 6, 9, 'Someone liked your answer', 0, '2026-01-02 10:04:06'),
(11, 1, 'like_question', 1, 13, 'Someone liked your question', 0, '2026-01-02 13:00:54'),
(12, 2, 'like_answer', 1, 13, 'Someone liked your answer', 0, '2026-01-02 13:00:56'),
(13, 7, 'like_answer', 2, 13, 'Someone liked your answer', 0, '2026-01-02 13:00:57'),
(16, 7, 'like_answer', 2, 13, 'Someone liked your answer', 0, '2026-01-02 13:21:17'),
(17, 1, 'like_question', 1, 13, 'Someone liked your question', 0, '2026-01-02 13:21:20'),
(18, 11, 'answer', 6, 9, 'Someone answered your question: What are all job opportunities after diploma?', 1, '2026-01-03 10:20:12'),
(19, 7, 'like_question', 2, 9, 'Someone liked your question', 0, '2026-01-03 10:40:08'),
(20, 7, 'like_question', 2, 9, 'Someone liked your question', 0, '2026-01-03 10:40:09'),
(21, 7, 'like_question', 2, 9, 'Someone liked your question', 0, '2026-01-03 10:40:11'),
(22, 7, 'like_question', 2, 9, 'Someone liked your question', 0, '2026-01-03 10:40:11'),
(23, 7, 'like_question', 2, 9, 'Someone liked your question', 0, '2026-01-03 10:40:11'),
(24, 7, 'like_question', 2, 9, 'Someone liked your question', 0, '2026-01-03 10:40:12'),
(25, 1, 'like_question', 1, 9, 'Someone liked your question', 0, '2026-01-03 10:40:14'),
(26, 7, 'like_question', 3, 9, 'Someone liked your question', 0, '2026-01-03 10:40:16'),
(27, 7, 'like_answer', 6, 9, 'Someone liked your answer', 0, '2026-01-03 10:40:21'),
(28, 11, 'answer', 8, 9, 'Someone answered your question: Hello', 1, '2026-01-03 10:44:37'),
(29, 11, 'answer', 8, 9, 'Someone answered your question: Hello', 1, '2026-01-03 11:00:20'),
(30, 9, 'answer', 9, 11, 'Someone answered your question: fsbend', 1, '2026-01-03 11:01:27'),
(31, 9, 'answer', 9, 11, 'Someone answered your question: fsbend', 1, '2026-01-03 11:07:11');

-- --------------------------------------------------------

--
-- Table structure for table `roadmaps`
--

CREATE TABLE `roadmaps` (
  `roadmap_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `roadmap_type` enum('USER','SYSTEM') NOT NULL DEFAULT 'USER',
  `start_education_level_id` int(11) DEFAULT NULL,
  `start_stream_id` int(11) DEFAULT NULL,
  `target_job_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `roadmaps`
--

INSERT INTO `roadmaps` (`roadmap_id`, `user_id`, `roadmap_type`, `start_education_level_id`, `start_stream_id`, `target_job_id`, `created_at`) VALUES
(1, 1, 'USER', 2, NULL, NULL, '2025-12-26 17:14:46'),
(2, 1, 'USER', 2, NULL, NULL, '2025-12-26 17:34:40'),
(3, 1, 'SYSTEM', NULL, 7, 2, '2025-12-26 17:47:10'),
(4, 1, 'SYSTEM', NULL, 1, 2, '2025-12-26 17:49:02'),
(5, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 17:49:28'),
(6, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 17:51:57'),
(7, 1, 'SYSTEM', NULL, 7, 2, '2025-12-26 17:52:40'),
(8, 1, 'SYSTEM', NULL, 2, 5, '2025-12-26 17:57:48'),
(9, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 18:12:14'),
(10, 1, 'SYSTEM', NULL, 4, 9, '2025-12-26 18:12:54'),
(11, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 18:18:37'),
(12, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 18:19:43'),
(13, 1, 'SYSTEM', NULL, 1, 2, '2025-12-26 18:19:54'),
(14, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 18:21:15'),
(15, 1, 'SYSTEM', NULL, 1, 2, '2025-12-26 18:21:20'),
(16, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 18:22:00'),
(17, 1, 'SYSTEM', NULL, 1, 2, '2025-12-26 18:23:12'),
(18, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 18:24:02'),
(19, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 18:44:52'),
(20, 1, 'SYSTEM', NULL, 1, 2, '2025-12-26 18:45:04'),
(21, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 18:45:21'),
(22, 1, 'SYSTEM', NULL, 1, 1, '2025-12-26 18:48:20'),
(23, 1, 'SYSTEM', NULL, 1, 2, '2025-12-26 18:48:51'),
(24, 1, 'SYSTEM', NULL, 1, 5, '2025-12-26 18:49:22'),
(25, 1, 'SYSTEM', NULL, 2, 5, '2025-12-26 18:49:44'),
(26, 1, 'USER', 1, 1, 1, '2025-12-26 19:03:45'),
(27, 4, 'SYSTEM', NULL, 10, 3, '2025-12-28 19:35:44'),
(28, 1, 'SYSTEM', NULL, 10, 3, '2025-12-28 19:39:32'),
(29, 4, 'SYSTEM', NULL, 10, 3, '2025-12-28 19:50:06'),
(30, 4, 'SYSTEM', NULL, 7, 1, '2025-12-28 19:50:59'),
(31, 4, 'SYSTEM', NULL, 11, 6, '2025-12-28 19:52:15'),
(32, 4, 'SYSTEM', NULL, 21, 9, '2025-12-28 19:52:38'),
(33, 4, 'SYSTEM', NULL, 10, 3, '2025-12-28 19:57:22'),
(34, 4, 'SYSTEM', NULL, 2, 8, '2025-12-28 20:06:53'),
(35, 4, 'SYSTEM', NULL, 2, 9, '2025-12-28 20:07:03'),
(36, 4, 'SYSTEM', NULL, 2, 3, '2025-12-28 20:07:11'),
(37, 4, 'SYSTEM', NULL, 2, 5, '2025-12-28 20:07:17'),
(38, 4, 'SYSTEM', NULL, 1, 8, '2025-12-28 20:07:45'),
(39, 4, 'SYSTEM', NULL, 1, 9, '2025-12-28 20:07:52'),
(40, 4, 'SYSTEM', NULL, 1, 3, '2025-12-28 20:07:58'),
(41, 4, 'SYSTEM', NULL, 2, 5, '2025-12-28 20:08:20'),
(42, 4, 'SYSTEM', NULL, 4, 13, '2025-12-28 20:08:49'),
(43, 4, 'SYSTEM', NULL, 4, 7, '2025-12-28 20:08:55'),
(44, 4, 'SYSTEM', NULL, 1, 1, '2025-12-28 20:09:38'),
(45, 4, 'SYSTEM', NULL, 1, 1, '2025-12-28 20:15:23'),
(46, 4, 'SYSTEM', NULL, 2, 5, '2025-12-28 20:15:45'),
(47, 4, 'SYSTEM', NULL, 1, 1, '2025-12-28 20:19:16'),
(48, 4, 'SYSTEM', NULL, 4, 7, '2025-12-28 20:19:48'),
(49, 4, 'SYSTEM', NULL, 2, 5, '2025-12-29 03:04:21'),
(50, 4, 'SYSTEM', NULL, 18, 1, '2025-12-29 03:36:48'),
(51, 4, 'SYSTEM', NULL, 23, 13, '2025-12-29 03:47:15'),
(52, 4, 'SYSTEM', NULL, 1, 2, '2025-12-29 04:00:11'),
(53, 4, 'SYSTEM', NULL, 4, 14, '2025-12-29 04:22:54'),
(54, 4, 'SYSTEM', NULL, 4, 13, '2025-12-29 04:23:00'),
(55, 4, 'SYSTEM', NULL, 4, 8, '2025-12-29 04:23:08'),
(56, 6, 'SYSTEM', NULL, 16, 11, '2025-12-29 09:19:45'),
(57, 6, 'SYSTEM', NULL, 4, 7, '2025-12-29 09:20:22'),
(58, 6, 'SYSTEM', NULL, 12, 9, '2025-12-30 08:21:55'),
(59, 6, 'SYSTEM', NULL, 16, 51, '2025-12-30 09:04:34'),
(60, 7, 'SYSTEM', NULL, 2, 5, '2025-12-30 16:30:22'),
(61, 7, 'SYSTEM', NULL, 1, 3, '2025-12-31 04:45:07'),
(62, 9, 'SYSTEM', NULL, 4, 30, '2026-01-02 10:04:44'),
(63, 9, 'SYSTEM', NULL, 4, 30, '2026-01-02 10:04:48'),
(64, 9, 'SYSTEM', NULL, 4, 30, '2026-01-02 10:04:58'),
(65, 9, 'SYSTEM', NULL, 4, 9, '2026-01-02 10:05:02'),
(66, 9, 'SYSTEM', NULL, 31, 9, '2026-01-02 10:56:29'),
(67, 9, 'SYSTEM', NULL, 10, 14, '2026-01-02 11:37:57'),
(68, 9, 'SYSTEM', NULL, 32, 14, '2026-01-02 11:40:11'),
(69, 9, 'SYSTEM', NULL, 16, 11, '2026-01-02 11:45:13'),
(70, 12, 'SYSTEM', NULL, 13, 9, '2026-01-02 12:01:14'),
(85, 11, 'SYSTEM', NULL, 16, 11, '2026-01-02 13:29:11'),
(87, 11, 'SYSTEM', NULL, 1, 3, '2026-01-02 13:46:01'),
(88, 11, 'SYSTEM', NULL, 20, 14, '2026-01-03 03:33:33'),
(89, 11, 'SYSTEM', NULL, 20, 13, '2026-01-03 03:52:17'),
(90, 11, 'SYSTEM', NULL, 24, 13, '2026-01-03 03:53:29'),
(91, 11, 'SYSTEM', NULL, 1, 1, '2026-01-03 04:26:44'),
(92, 11, 'SYSTEM', NULL, 14, 7, '2026-01-03 04:30:32'),
(94, 11, 'SYSTEM', NULL, 22, 13, '2026-01-03 04:36:06'),
(95, 1, '', NULL, 1, 1, '2026-01-04 08:51:23'),
(96, 1, '', NULL, 1, 1, '2026-01-04 08:59:54'),
(97, 1, '', NULL, 1, 1, '2026-01-05 03:43:05'),
(98, 1, '', NULL, 1, 1, '2026-01-05 03:47:06'),
(99, 1, '', NULL, 1, 1, '2026-01-05 07:09:59'),
(100, 1, '', NULL, 1, 1, '2026-01-05 07:10:15'),
(105, 9, 'SYSTEM', NULL, 2, 77, '2026-01-05 07:44:18'),
(106, 9, 'SYSTEM', NULL, 1, 56, '2026-01-05 07:44:55'),
(107, 9, 'SYSTEM', NULL, 1, 53, '2026-01-05 08:03:42'),
(108, 9, 'SYSTEM', NULL, 1, 2, '2026-01-05 08:15:50'),
(109, 9, 'SYSTEM', NULL, 1, 3, '2026-01-05 08:17:31'),
(111, 9, 'SYSTEM', NULL, 1, 1, '2026-01-05 08:21:36');

-- --------------------------------------------------------

--
-- Table structure for table `roadmap_steps`
--

CREATE TABLE `roadmap_steps` (
  `step_id` int(11) NOT NULL,
  `roadmap_id` int(11) DEFAULT NULL,
  `step_order` int(11) DEFAULT NULL,
  `step_type` enum('Education','Stream','Exam','Degree','Experience','Preparation','Job') DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `icon` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `roadmap_steps`
--

INSERT INTO `roadmap_steps` (`step_id`, `roadmap_id`, `step_order`, `step_type`, `title`, `description`, `icon`) VALUES
(2, 2, 1, 'Stream', 'Engineering (B.Tech)', 'Choose Engineering stream after 12th Science', 'ic_engineering'),
(3, 3, 1, 'Stream', 'Engineering (B.Tech)', 'Current stream selected', 'ic_stream'),
(4, 3, 2, 'Job', 'Data Analyst', 'Target job goal', 'ic_job'),
(5, 4, 1, 'Stream', 'Science Stream (PCM)', 'Current stream selected', 'ic_stream'),
(6, 4, 2, 'Job', 'Data Analyst', 'Target job goal', 'ic_job'),
(7, 5, 1, 'Stream', 'Science Stream (PCM)', 'Current stream selected', 'ic_stream'),
(8, 5, 2, 'Job', 'Software Engineer', 'Target job goal', 'ic_job'),
(9, 17, 1, 'Stream', 'Science Stream (PCM)', 'Education stream', 'ic_stream'),
(10, 17, 2, 'Exam', 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance examination', 'ic_exam'),
(11, 17, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(12, 17, 4, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(13, 17, 5, 'Exam', 'JEE Advanced', 'Entrance examination', 'ic_exam'),
(14, 17, 6, 'Exam', 'BITSAT', 'Entrance examination', 'ic_exam'),
(15, 17, 7, 'Stream', 'Law (LLB)', 'Education stream', 'ic_stream'),
(16, 17, 8, 'Exam', 'CLAT', 'Entrance examination', 'ic_exam'),
(17, 17, 9, 'Stream', 'Advanced Diploma', 'Education stream', 'ic_stream'),
(18, 17, 10, 'Job', 'Data Analyst', 'Target career', 'ic_job'),
(19, 18, 1, 'Stream', 'Science Stream (PCM)', 'Education stream', 'ic_stream'),
(20, 18, 2, 'Exam', 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance examination', 'ic_exam'),
(21, 18, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(22, 18, 4, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(23, 18, 5, 'Exam', 'JEE Advanced', 'Entrance examination', 'ic_exam'),
(24, 18, 6, 'Exam', 'BITSAT', 'Entrance examination', 'ic_exam'),
(25, 18, 7, 'Stream', 'Law (LLB)', 'Education stream', 'ic_stream'),
(26, 18, 8, 'Exam', 'CLAT', 'Entrance examination', 'ic_exam'),
(27, 18, 9, 'Stream', 'Advanced Diploma', 'Education stream', 'ic_stream'),
(28, 18, 10, 'Job', 'Software Engineer', 'Target career', 'ic_job'),
(29, 19, 1, 'Stream', 'Science Stream (PCM)', 'Education stream', 'ic_stream'),
(30, 19, 2, 'Exam', 'National Means-cum-Merit Scholarship (NMMS)', 'Mandatory entrance exam', 'ic_exam'),
(31, 19, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(32, 19, 4, 'Exam', 'JEE Main', 'Mandatory entrance exam', 'ic_exam'),
(33, 19, 5, 'Exam', 'JEE Advanced', 'Mandatory entrance exam', 'ic_exam'),
(34, 19, 6, 'Exam', 'BITSAT', 'Mandatory entrance exam', 'ic_exam'),
(35, 19, 7, 'Stream', 'BA', 'Education stream', 'ic_stream'),
(36, 19, 8, 'Exam', 'CUET (UG)', 'Mandatory entrance exam', 'ic_exam'),
(37, 19, 9, 'Stream', 'Advanced Diploma', 'Education stream', 'ic_stream'),
(38, 20, 1, 'Stream', 'Science Stream (PCM)', 'Education stream', 'ic_stream'),
(39, 20, 2, 'Exam', 'National Means-cum-Merit Scholarship (NMMS)', 'Mandatory entrance exam', 'ic_exam'),
(40, 20, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(41, 20, 4, 'Exam', 'JEE Main', 'Mandatory entrance exam', 'ic_exam'),
(42, 20, 5, 'Exam', 'JEE Advanced', 'Mandatory entrance exam', 'ic_exam'),
(43, 20, 6, 'Exam', 'BITSAT', 'Mandatory entrance exam', 'ic_exam'),
(44, 20, 7, 'Stream', 'BA', 'Education stream', 'ic_stream'),
(45, 20, 8, 'Exam', 'CUET (UG)', 'Mandatory entrance exam', 'ic_exam'),
(46, 20, 9, 'Stream', 'Advanced Diploma', 'Education stream', 'ic_stream'),
(47, 21, 1, 'Stream', 'Science Stream (PCM)', 'Education stream', 'ic_stream'),
(48, 21, 2, 'Exam', 'National Means-cum-Merit Scholarship (NMMS)', 'Mandatory entrance exam', 'ic_exam'),
(49, 21, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(50, 21, 4, 'Exam', 'JEE Main', 'Mandatory entrance exam', 'ic_exam'),
(51, 21, 5, 'Exam', 'JEE Advanced', 'Mandatory entrance exam', 'ic_exam'),
(52, 21, 6, 'Exam', 'BITSAT', 'Mandatory entrance exam', 'ic_exam'),
(53, 21, 7, 'Stream', 'BA', 'Education stream', 'ic_stream'),
(54, 21, 8, 'Exam', 'CUET (UG)', 'Mandatory entrance exam', 'ic_exam'),
(55, 21, 9, 'Stream', 'Advanced Diploma', 'Education stream', 'ic_stream'),
(56, 22, 1, 'Stream', 'Science Stream (PCM)', 'Education stream', 'ic_stream'),
(57, 22, 2, 'Exam', 'National Means-cum-Merit Scholarship (NMMS)', 'Mandatory entrance exam', 'ic_exam'),
(58, 22, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(59, 22, 4, 'Exam', 'JEE Main', 'Mandatory entrance exam', 'ic_exam'),
(60, 22, 5, 'Exam', 'JEE Advanced', 'Mandatory entrance exam', 'ic_exam'),
(61, 22, 6, 'Exam', 'BITSAT', 'Mandatory entrance exam', 'ic_exam'),
(62, 22, 7, 'Job', 'Software Engineer', 'Target career goal', 'ic_job'),
(63, 23, 1, 'Stream', 'Science Stream (PCM)', 'Education stream', 'ic_stream'),
(64, 23, 2, 'Exam', 'National Means-cum-Merit Scholarship (NMMS)', 'Mandatory entrance exam', 'ic_exam'),
(65, 23, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(66, 23, 4, 'Exam', 'JEE Main', 'Mandatory entrance exam', 'ic_exam'),
(67, 23, 5, 'Exam', 'JEE Advanced', 'Mandatory entrance exam', 'ic_exam'),
(68, 23, 6, 'Exam', 'BITSAT', 'Mandatory entrance exam', 'ic_exam'),
(69, 23, 7, 'Job', 'Data Analyst', 'Target career goal', 'ic_job'),
(70, 24, 1, 'Stream', 'Science Stream (PCM)', 'Education stream', 'ic_stream'),
(71, 24, 2, 'Exam', 'National Means-cum-Merit Scholarship (NMMS)', 'Mandatory entrance exam', 'ic_exam'),
(72, 24, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(73, 24, 4, 'Exam', 'JEE Main', 'Mandatory entrance exam', 'ic_exam'),
(74, 24, 5, 'Exam', 'JEE Advanced', 'Mandatory entrance exam', 'ic_exam'),
(75, 24, 6, 'Exam', 'BITSAT', 'Mandatory entrance exam', 'ic_exam'),
(76, 24, 7, 'Stream', 'BA', 'Education stream', 'ic_stream'),
(77, 24, 8, 'Exam', 'CUET (UG)', 'Mandatory entrance exam', 'ic_exam'),
(78, 24, 9, 'Stream', 'Advanced Diploma', 'Education stream', 'ic_stream'),
(79, 25, 1, 'Stream', 'Science Stream (PCB)', 'Education stream', 'ic_stream'),
(80, 25, 2, 'Exam', 'National Means-cum-Merit Scholarship (NMMS)', 'Mandatory entrance exam', 'ic_exam'),
(81, 25, 3, 'Stream', 'Medical (MBBS / BDS)', 'Education stream', 'ic_stream'),
(82, 25, 4, 'Exam', 'NEET', 'Mandatory entrance exam', 'ic_exam'),
(83, 25, 5, 'Job', 'Doctor (MBBS)', 'Target career goal', 'ic_job'),
(84, 26, 1, 'Stream', 'Science Stream (PCM)', 'Starting stream selected', 'ic_stream'),
(85, 26, 2, 'Exam', 'NMMS', 'Scholarship exam after 10th', 'ic_exam'),
(86, 26, 3, 'Stream', 'Engineering (B.Tech)', 'UG Engineering degree', 'ic_stream'),
(87, 26, 4, 'Exam', 'JEE Main', 'Engineering entrance exam', 'ic_exam'),
(88, 26, 5, 'Job', 'Software Engineer', 'Final career goal', 'ic_job'),
(89, 27, 1, 'Education', '12th Science', 'Current education level', 'ic_education'),
(90, 27, 2, 'Stream', 'Architecture (B.Arch)', 'Selected academic stream', 'ic_stream'),
(91, 27, 3, '', 'Higher Education', 'Continue your academic journey', 'ic_graduation'),
(92, 27, 4, 'Experience', 'Internships & Projects', 'Gain practical experience and build a real-world portfolio.', 'ic_briefcase'),
(93, 27, 5, 'Preparation', 'Interviews & Apps', 'Resume building, mock interviews, and technical screening prep.', 'ic_document'),
(94, 27, 6, 'Job', 'Civil Engineer', 'Achieve your dream role at a top company.', 'ic_briefcase'),
(95, 28, 1, 'Stream', 'Architecture (B.Arch)', 'Education stream', 'ic_stream'),
(96, 28, 2, 'Exam', 'JEE Main', 'Mandatory entrance exam', 'ic_exam'),
(97, 28, 3, 'Exam', 'NATA', 'Mandatory entrance exam', 'ic_exam'),
(98, 28, 4, 'Job', 'Civil Engineer', 'Target career goal', 'ic_job'),
(99, 29, 1, 'Stream', 'Architecture (B.Arch)', 'Education stream', 'ic_stream'),
(100, 29, 2, 'Exam', 'JEE Main', 'Mandatory entrance exam', 'ic_exam'),
(101, 29, 3, 'Exam', 'NATA', 'Mandatory entrance exam', 'ic_exam'),
(102, 29, 4, 'Job', 'Civil Engineer', 'Target career goal', 'ic_job'),
(103, 30, 1, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(104, 30, 2, 'Exam', 'JEE Main', 'Mandatory entrance exam', 'ic_exam'),
(105, 30, 3, 'Job', 'Software Engineer', 'Target career goal', 'ic_job'),
(106, 31, 1, 'Stream', 'Pharmacy (B.Pharm)', 'Education stream', 'ic_stream'),
(107, 31, 2, 'Exam', 'CUET (UG)', 'Mandatory entrance exam', 'ic_exam'),
(108, 31, 3, 'Job', 'Pharmacist', 'Target career goal', 'ic_job'),
(109, 32, 1, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(110, 32, 2, 'Exam', 'CAT', 'Mandatory entrance exam', 'ic_exam'),
(111, 32, 3, 'Job', 'Business Analyst', 'Target career goal', 'ic_job'),
(112, 33, 1, 'Stream', 'Architecture (B.Arch)', 'Education stream', 'ic_stream'),
(113, 33, 2, 'Exam', 'JEE Main or NATA', 'Entrance examination', 'ic_exam'),
(114, 33, 3, 'Job', 'Civil Engineer', 'Target career goal', 'ic_job'),
(115, 34, 1, 'Stream', 'Science (PCB)', 'Education stream', 'ic_stream'),
(116, 34, 2, 'Stream', 'Medical (MBBS / BDS)', 'Education stream', 'ic_stream'),
(117, 34, 3, 'Exam', 'NEET', 'Entrance examination', 'ic_exam'),
(118, 34, 4, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(119, 34, 5, 'Exam', 'NET', 'Entrance examination', 'ic_exam'),
(120, 35, 1, 'Stream', 'Science (PCB)', 'Education stream', 'ic_stream'),
(121, 35, 2, 'Stream', 'Medical (MBBS / BDS)', 'Education stream', 'ic_stream'),
(122, 35, 3, 'Exam', 'NEET', 'Entrance examination', 'ic_exam'),
(123, 35, 4, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(124, 35, 5, 'Exam', 'NET', 'Entrance examination', 'ic_exam'),
(125, 36, 1, 'Stream', 'Science (PCB)', 'Education stream', 'ic_stream'),
(126, 36, 2, 'Stream', 'Medical (MBBS / BDS)', 'Education stream', 'ic_stream'),
(127, 36, 3, 'Exam', 'NEET', 'Entrance examination', 'ic_exam'),
(128, 36, 4, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(129, 36, 5, 'Exam', 'NET', 'Entrance examination', 'ic_exam'),
(130, 37, 1, 'Stream', 'Science (PCB)', 'Education stream', 'ic_stream'),
(131, 37, 2, 'Stream', 'Medical (MBBS / BDS)', 'Education stream', 'ic_stream'),
(132, 37, 3, 'Exam', 'NEET', 'Entrance examination', 'ic_exam'),
(133, 37, 4, 'Job', 'Doctor (MBBS)', 'Target career goal', 'ic_job'),
(134, 38, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(135, 38, 2, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(136, 38, 3, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(137, 38, 4, 'Stream', 'M.Tech', 'Education stream', 'ic_stream'),
(138, 38, 5, 'Exam', 'GATE', 'Entrance examination', 'ic_exam'),
(139, 39, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(140, 39, 2, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(141, 39, 3, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(142, 39, 4, 'Stream', 'M.Tech', 'Education stream', 'ic_stream'),
(143, 39, 5, 'Exam', 'GATE', 'Entrance examination', 'ic_exam'),
(144, 40, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(145, 40, 2, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(146, 40, 3, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(147, 40, 4, 'Job', 'Civil Engineer', 'Target career goal', 'ic_job'),
(148, 41, 1, 'Stream', 'Science (PCB)', 'Education stream', 'ic_stream'),
(149, 41, 2, 'Stream', 'Medical (MBBS / BDS)', 'Education stream', 'ic_stream'),
(150, 41, 3, 'Exam', 'NEET', 'Entrance examination', 'ic_exam'),
(151, 41, 4, 'Job', 'Doctor (MBBS)', 'Target career goal', 'ic_job'),
(152, 42, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(153, 42, 2, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(154, 42, 3, 'Exam', 'CUET (UG)', 'Entrance examination', 'ic_exam'),
(155, 42, 4, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(156, 42, 5, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(157, 43, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(158, 43, 2, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(159, 43, 3, 'Exam', 'CUET (UG)', 'Entrance examination', 'ic_exam'),
(160, 43, 4, 'Job', 'Chartered Accountant', 'Target career goal', 'ic_job'),
(161, 44, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(162, 44, 2, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(163, 44, 3, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(164, 44, 4, 'Job', 'Software Engineer', 'Target career goal', 'ic_job'),
(165, 45, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(166, 45, 2, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(167, 45, 3, 'Job', 'Software Engineer', 'Target career goal', 'ic_job'),
(168, 46, 1, 'Stream', 'Science (PCB)', 'Education stream', 'ic_stream'),
(169, 46, 2, 'Stream', 'Medical (MBBS / BDS)', 'Education stream', 'ic_stream'),
(170, 46, 3, 'Job', 'Doctor (MBBS)', 'Target career goal', 'ic_job'),
(171, 47, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(172, 47, 2, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(173, 47, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(174, 47, 4, 'Job', 'Software Engineer', 'Target career goal', 'ic_job'),
(175, 48, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(176, 48, 2, 'Exam', 'CUET (UG)', 'Entrance examination', 'ic_exam'),
(177, 48, 3, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(178, 48, 4, 'Job', 'Chartered Accountant', 'Target career goal', 'ic_job'),
(179, 49, 1, 'Stream', 'Science (PCB)', 'Education stream', 'ic_stream'),
(180, 49, 2, 'Exam', 'NEET', 'Entrance examination', 'ic_exam'),
(181, 49, 3, 'Stream', 'Medical (MBBS / BDS)', 'Education stream', 'ic_stream'),
(182, 49, 4, 'Job', 'Doctor (MBBS)', 'Target career goal', 'ic_job'),
(183, 50, 1, 'Stream', 'Diploma', 'Education stream', 'ic_stream'),
(184, 50, 2, 'Exam', 'Lateral Entry Entrance Test (LEET)', 'Entrance examination', 'ic_exam'),
(185, 50, 3, 'Stream', 'B.Tech (Lateral Entry)', 'Education stream', 'ic_stream'),
(186, 50, 4, 'Job', 'Software Engineer', 'Target career goal', 'ic_job'),
(187, 51, 1, 'Stream', 'M.Sc', 'Education stream', 'ic_stream'),
(188, 51, 2, 'Job', 'Professor', 'Target career goal', 'ic_job'),
(189, 52, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(190, 52, 2, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(191, 52, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(192, 52, 4, 'Job', 'Data Analyst', 'Target career goal', 'ic_job'),
(193, 53, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(194, 53, 2, 'Exam', 'CUET (UG)', 'Entrance examination', 'ic_exam'),
(195, 53, 3, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(196, 53, 4, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(197, 53, 5, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(198, 54, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(199, 54, 2, 'Exam', 'CUET (UG)', 'Entrance examination', 'ic_exam'),
(200, 54, 3, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(201, 54, 4, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(202, 54, 5, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(203, 55, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(204, 55, 2, 'Exam', 'CUET (UG)', 'Entrance examination', 'ic_exam'),
(205, 55, 3, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(206, 55, 4, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(207, 55, 5, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(208, 55, 6, 'Job', 'Bank Probationary Officer', 'Target career goal', 'ic_job'),
(209, 56, 1, 'Stream', 'Law (LLB)', 'Education stream', 'ic_stream'),
(210, 56, 2, 'Job', 'Lawyer', 'Target career goal', 'ic_job'),
(211, 57, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(212, 57, 2, 'Exam', 'CUET (UG)', 'Entrance examination', 'ic_exam'),
(213, 57, 3, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(214, 57, 4, 'Job', 'Chartered Accountant', 'Target career goal', 'ic_job'),
(215, 58, 1, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(216, 58, 2, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(217, 58, 3, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(218, 58, 4, 'Job', 'Business Analyst', 'Target career goal', 'ic_job'),
(219, 59, 1, 'Stream', 'Law (LLB)', 'Education stream', 'ic_stream'),
(220, 59, 2, 'Exam', 'Common Law Admission Test – PG (CLAT-PG)', 'Entrance examination', 'ic_exam'),
(221, 59, 3, 'Stream', 'LLM (Master of Laws)', 'Education stream', 'ic_stream'),
(222, 59, 4, 'Job', 'Legal Advisor in Govt / PSUs', 'Target career goal', 'ic_job'),
(223, 60, 1, 'Stream', 'Science (PCB)', 'Education stream', 'ic_stream'),
(224, 60, 2, 'Exam', 'NEET-UG', 'Entrance examination', 'ic_exam'),
(225, 60, 3, 'Stream', 'Medical (MBBS / BDS)', 'Education stream', 'ic_stream'),
(226, 60, 4, 'Job', 'Doctor (MBBS)', 'Target career goal', 'ic_job'),
(227, 61, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(228, 61, 2, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(229, 61, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(230, 61, 4, 'Job', 'Civil Engineer', 'Target career goal', 'ic_job'),
(231, 62, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(232, 62, 2, 'Exam', 'CUET-UG (Commerce)', 'Entrance examination', 'ic_exam'),
(233, 62, 3, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(234, 62, 4, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(235, 62, 5, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(236, 62, 6, 'Exam', 'UGC National Eligibility Test (UGC NET) or CSIR National Eligibility Test (CSIR NET) or State Eligib', 'Entrance examination', 'ic_exam'),
(237, 62, 7, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(238, 62, 8, 'Stream', 'Post-Doctoral Fellowship (Post-Doc)', 'Education stream', 'ic_stream'),
(239, 63, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(240, 63, 2, 'Exam', 'CUET-UG (Commerce)', 'Entrance examination', 'ic_exam'),
(241, 63, 3, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(242, 63, 4, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(243, 63, 5, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(244, 63, 6, 'Exam', 'UGC National Eligibility Test (UGC NET) or CSIR National Eligibility Test (CSIR NET) or State Eligib', 'Entrance examination', 'ic_exam'),
(245, 63, 7, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(246, 63, 8, 'Stream', 'Post-Doctoral Fellowship (Post-Doc)', 'Education stream', 'ic_stream'),
(247, 64, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(248, 64, 2, 'Exam', 'CUET-UG (Commerce)', 'Entrance examination', 'ic_exam'),
(249, 64, 3, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(250, 64, 4, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(251, 64, 5, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(252, 64, 6, 'Exam', 'UGC National Eligibility Test (UGC NET) or CSIR National Eligibility Test (CSIR NET) or State Eligib', 'Entrance examination', 'ic_exam'),
(253, 64, 7, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(254, 64, 8, 'Stream', 'Post-Doctoral Fellowship (Post-Doc)', 'Education stream', 'ic_stream'),
(255, 65, 1, 'Stream', 'Commerce', 'Education stream', 'ic_stream'),
(256, 65, 2, 'Exam', 'CUET-UG (Commerce)', 'Entrance examination', 'ic_exam'),
(257, 65, 3, 'Stream', 'B.Com', 'Education stream', 'ic_stream'),
(258, 65, 4, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(259, 65, 5, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(260, 65, 6, 'Job', 'Business Analyst', 'Target career goal', 'ic_job'),
(261, 66, 1, 'Stream', 'BHM (Bachelor of Hotel Management)', 'Education stream', 'ic_stream'),
(262, 66, 2, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(263, 66, 3, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(264, 66, 4, 'Job', 'Business Analyst', 'Target career goal', 'ic_job'),
(265, 67, 1, 'Stream', 'Architecture (B.Arch)', 'Education stream', 'ic_stream'),
(266, 67, 2, 'Exam', 'GATE', 'Entrance examination', 'ic_exam'),
(267, 67, 3, 'Stream', 'M.Tech', 'Education stream', 'ic_stream'),
(268, 67, 4, 'Exam', 'UGC National Eligibility Test (UGC NET) or CSIR National Eligibility Test (CSIR NET) or State Eligib', 'Entrance examination', 'ic_exam'),
(269, 67, 5, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(270, 67, 6, 'Job', 'Research Scientist', 'Target career goal', 'ic_job'),
(271, 68, 1, 'Stream', 'BCA (Bachelor of Computer Applications)', 'Education stream', 'ic_stream'),
(272, 68, 2, 'Exam', 'CAT', 'Entrance examination', 'ic_exam'),
(273, 68, 3, 'Stream', 'MBA', 'Education stream', 'ic_stream'),
(274, 68, 4, 'Exam', 'UGC National Eligibility Test (UGC NET) or CSIR National Eligibility Test (CSIR NET) or State Eligib', 'Entrance examination', 'ic_exam'),
(275, 68, 5, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(276, 68, 6, 'Job', 'Research Scientist', 'Target career goal', 'ic_job'),
(277, 69, 1, 'Stream', 'Law (LLB)', 'Education stream', 'ic_stream'),
(278, 69, 2, 'Job', 'Lawyer', 'Target career goal', 'ic_job'),
(279, 70, 1, 'Stream', 'BBA (Commerce Stream)', 'Education stream', 'ic_stream'),
(280, 70, 2, 'Job', 'Business Analyst', 'Target career goal', 'ic_job'),
(361, 85, 1, 'Stream', 'Law (LLB)', 'Education stream', 'ic_stream'),
(362, 85, 2, 'Job', 'Lawyer', 'Target career goal', 'ic_job'),
(371, 87, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(372, 87, 2, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(373, 87, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(374, 87, 4, 'Job', 'Civil Engineer', 'Target career goal', 'ic_job'),
(375, 88, 1, 'Stream', 'B.Tech (Lateral Entry)', 'Education stream', 'ic_stream'),
(376, 88, 2, 'Exam', 'GATE', 'Entrance examination', 'ic_exam'),
(377, 88, 3, 'Stream', 'M.Tech', 'Education stream', 'ic_stream'),
(378, 88, 4, 'Exam', 'UGC National Eligibility Test (UGC NET) or CSIR National Eligibility Test (CSIR NET) or State Eligib', 'Entrance examination', 'ic_exam'),
(379, 88, 5, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(380, 88, 6, 'Job', 'Research Scientist', 'Target career goal', 'ic_job'),
(381, 89, 1, 'Stream', 'B.Tech (Lateral Entry)', 'Education stream', 'ic_stream'),
(382, 89, 2, 'Exam', 'GATE', 'Entrance examination', 'ic_exam'),
(383, 89, 3, 'Stream', 'M.Tech', 'Education stream', 'ic_stream'),
(384, 89, 4, 'Exam', 'UGC National Eligibility Test (UGC NET) or CSIR National Eligibility Test (CSIR NET) or State Eligib', 'Entrance examination', 'ic_exam'),
(385, 89, 5, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(386, 89, 6, 'Job', 'Professor', 'Target career goal', 'ic_job'),
(387, 90, 1, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(388, 90, 2, 'Job', 'Professor', 'Target career goal', 'ic_job'),
(389, 91, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(390, 91, 2, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(391, 91, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(392, 91, 4, 'Job', 'Software Engineer', 'Target career goal', 'ic_job'),
(393, 92, 1, 'Stream', 'Chartered Accountant(CA)', 'Education stream', 'ic_stream'),
(394, 92, 2, 'Job', 'Chartered Accountant', 'Target career goal', 'ic_job'),
(399, 94, 1, 'Stream', 'M.Tech', 'Education stream', 'ic_stream'),
(400, 94, 2, 'Exam', 'UGC National Eligibility Test (UGC NET) or CSIR National Eligibility Test (CSIR NET) or State Eligib', 'Entrance examination', 'ic_exam'),
(401, 94, 3, 'Stream', 'PhD', 'Education stream', 'ic_stream'),
(402, 94, 4, 'Job', 'Professor', 'Target career goal', 'ic_job'),
(403, 97, 1, 'Stream', 'TEST STREAM', 'TEST DESC', 'ic_test'),
(436, 105, 1, 'Stream', 'Science (PCB)', 'Education stream', 'ic_stream'),
(437, 105, 2, 'Job', 'Medical Lab Assistant', 'Target career goal', 'ic_job'),
(438, 106, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(439, 106, 2, 'Job', 'SSC Multi Tasking Staff (MTS)', 'Target career goal', 'ic_job'),
(440, 107, 1, 'Stream', 'Science (PCM)', 'Education Stream', 'ic_stream'),
(441, 107, 2, 'Job', 'Police Constable', 'Target Goal', 'ic_job'),
(442, 108, 1, 'Stream', 'Science (PCM)', 'Education Stream', 'ic_stream'),
(443, 108, 2, 'Exam', 'CUET-UG-Science', 'Entrance Exam', 'ic_exam'),
(444, 108, 3, 'Stream', 'Pure Sciences (B.Sc)', 'Education Stream', 'ic_stream'),
(445, 108, 4, 'Job', 'Data Analyst', 'Target Goal', 'ic_job'),
(446, 109, 1, 'Stream', 'Science (PCM)', 'Education Stream', 'ic_stream'),
(447, 109, 2, 'Exam', 'NATA', 'Entrance Exam', 'ic_exam'),
(448, 109, 3, 'Stream', 'Architecture (B.Arch)', 'Education Stream', 'ic_stream'),
(449, 109, 4, 'Job', 'Civil Engineer', 'Target Goal', 'ic_job'),
(458, 111, 1, 'Stream', 'Science (PCM)', 'Education stream', 'ic_stream'),
(459, 111, 2, 'Exam', 'JEE Main', 'Entrance examination', 'ic_exam'),
(460, 111, 3, 'Stream', 'Engineering (B.Tech)', 'Education stream', 'ic_stream'),
(461, 111, 4, 'Job', 'Software Engineer', 'Target career goal', 'ic_job');

-- --------------------------------------------------------

--
-- Table structure for table `saved_exams`
--

CREATE TABLE `saved_exams` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `exam_id` int(11) DEFAULT NULL,
  `stream_id` int(11) NOT NULL,
  `saved_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `saved_jobs`
--

CREATE TABLE `saved_jobs` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `job_id` int(11) NOT NULL,
  `stream_id` int(11) NOT NULL,
  `saved_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `saved_jobs`
--

INSERT INTO `saved_jobs` (`id`, `user_id`, `job_id`, `stream_id`, `saved_at`) VALUES
(1, 1, 2, 7, '2025-12-26 17:05:07');

-- --------------------------------------------------------

--
-- Table structure for table `saved_roadmaps`
--

CREATE TABLE `saved_roadmaps` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `roadmap_id` int(11) DEFAULT NULL,
  `saved_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `streams`
--

CREATE TABLE `streams` (
  `stream_id` int(11) NOT NULL,
  `education_level_id` int(11) NOT NULL,
  `stream_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `subjects` text DEFAULT NULL,
  `who_should_choose` text DEFAULT NULL,
  `career_scope` text DEFAULT NULL,
  `duration` varchar(50) DEFAULT NULL,
  `difficulty_level` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `streams`
--

INSERT INTO `streams` (`stream_id`, `education_level_id`, `stream_name`, `description`, `subjects`, `who_should_choose`, `career_scope`, `duration`, `difficulty_level`) VALUES
(1, 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'Physics, Chemistry, Mathematics', 'Interested in engineering & technology', 'Engineering, Architecture, Research', '2 Years', 'Hard'),
(2, 1, 'Science (PCB)', 'Science with Physics, Chemistry, Biology', 'Physics, Chemistry, Biology', 'Interested in medicine & life sciences', 'Medical, Research, Healthcare', '2 Years', 'Hard'),
(3, 1, 'Science (PCMB)', 'Science with PCM + Biology', 'Physics, Chemistry, Mathematics, Biology', 'Want flexibility between engineering & medical', 'Engineering, Medical, Research', '2 Years', 'Hard'),
(4, 1, 'Commerce', 'Business & finance oriented stream', 'Accountancy, Economics, Business Studies', 'Interested in business & finance', 'CA, MBA, Corporate Jobs', '2 Years', 'Medium'),
(5, 1, 'Arts / Humanities', 'Humanities & social sciences', 'History, Political Science, Sociology', 'Interested in arts, law & civil services', 'UPSC, Law, Media', '2 Years', 'Medium'),
(6, 1, 'Vocational / ITI', 'Skill-based vocational education', 'Technical & Trade Skills', 'Prefer practical learning', 'Skilled Jobs, Diploma', '1–2 Years', 'Easy'),
(7, 2, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'Mathematics, Engineering Subjects', 'Interested in technology & innovation', 'Software, Core Engineering, Govt Jobs', '4 Years', 'Hard'),
(8, 2, 'Medical (MBBS / BDS)', 'Professional medical education', 'Biology, Medicine', 'Interested in healthcare', 'Doctor, Surgeon, Healthcare', '5–6 Years', 'Hard'),
(9, 2, 'Pure Sciences (B.Sc)', 'Academic science degree', 'Physics, Chemistry, Maths, Biology', 'Interested in research & teaching', 'Research, Teaching', '3 Years', 'Medium'),
(10, 2, 'Architecture (B.Arch)', 'Architecture & design degree', 'Mathematics, Design', 'Interested in design & construction', 'Architect, Urban Planner', '5 Years', 'Hard'),
(11, 2, 'Pharmacy (B.Pharm)', 'Pharmaceutical sciences', 'Chemistry, Pharmacology', 'Interested in medicines', 'Pharmacist, Pharma Industry', '4 Years', 'Medium'),
(12, 3, 'B.Com', 'Commerce undergraduate degree', 'Accountancy, Finance, Economics', 'Interested in accounting & finance', 'CA, MBA, Banking', '3 Years', 'Medium'),
(13, 3, 'BBA(Commerce)', 'Bachelor of Business Administration - An undergraduate program designed for commerce students that focuses on business management, finance.', 'Management, Marketing, Finance', 'Interested in management', 'MBA, Corporate Management', '3 Years', 'Medium'),
(14, 3, 'Chartered Accountant(CA)', 'Professional accounting qualification', 'Accounting, Taxation, Auditing', 'Interested in Accounting', 'CA Practice, Corporate Finance', '4-5 Years', 'Hard'),
(15, 4, 'BA', 'Arts undergraduate degree', 'Humanities Subjects', 'Interested in arts & civil services', 'UPSC, Teaching', '3 Years', 'Medium'),
(16, 4, 'Law (LLB)', 'Professional law degree', 'Law Subjects', 'Interested in legal careers', 'Lawyer, Judiciary', '5 Years', 'Hard'),
(17, 4, 'Mass Communication', 'Media & communication studies', 'Journalism, Media', 'Interested in media', 'Journalism, Media Industry', '3 Years', 'Medium'),
(19, 5, 'Advanced Diploma', 'Specialized diploma programs', 'Advanced Technical Skills', 'Skill upgradation', 'Supervisor Roles', '1 Year', 'Medium'),
(20, 5, 'B.Tech (Lateral Entry)', 'Engineering via lateral entry', 'Engineering Subjects', 'Diploma holders', 'Engineering Jobs', '3 Years', 'Hard'),
(21, 6, 'MBA', 'Postgraduate management degree', 'Management, HR, Finance', 'Leadership aspirants', 'Managerial Roles', '2 Years', 'Medium'),
(22, 6, 'M.Tech', 'Postgraduate engineering degree', 'Advanced Engineering', 'Engineering graduates', 'Senior Engineer', '2 Years', 'Hard'),
(23, 6, 'M.Sc', 'Postgraduate science degree', 'Advanced Sciences', 'Science graduates', 'Research, Teaching', '2 Years', 'Medium'),
(24, 7, 'PhD', 'Doctoral research program', 'Research Methodology', 'Research-focused students', 'Professor, Scientist', '3–5 Years', 'Hard'),
(25, 7, 'Professional Certification', 'Industry certifications', 'Domain Skills', 'Working professionals', 'Senior Professional Roles', '6–12 Months', 'Easy'),
(26, 3, 'BMS ', 'Bachelor of Management Studies-Undergraduate program focused on business management and leadership skills', 'Management Principles, Marketing, Finance, HR, Economics', 'Students interested in management, leadership, and business roles', 'Manager, Business Analyst, MBA, Corporate Roles', '3 Years', 'Medium'),
(27, 3, 'Company Secretary (CS)', 'Professional course focused on corporate law, governance, and compliance', 'Company Law, Corporate Governance, Business Laws, Taxation', 'Students interested in corporate law and company administration', 'Company Secretary, Legal Advisor, Compliance Officer', '3–4 Years', 'Hard'),
(28, 3, 'CMA', 'Cost & Management Accountant-Professional course focused on cost control, financial planning, and management accounting', 'Cost Accounting, Financial Management, Taxation, Corporate Laws', 'Students interested in finance, costing, and corporate strategy', 'Cost Accountant, Finance Manager, Corporate Finance Roles', '3–4 Years', 'Hard'),
(29, 4, 'BBA (Arts Stream)', 'Undergraduate program focused on business administration and management principles.', 'Management, Marketing, Finance, HR, Economics', 'Students interested in business, management, and entrepreneurship', 'Managerial Roles, MBA, Corporate & Startup Careers', '3 Years', 'Medium'),
(30, 4, 'BFA', 'Bachelor of Fine Arts - Undergraduate program focused on visual and performing arts.', 'Drawing, Painting, Sculpture, Visual Arts, Design', 'Students with strong creativity and interest in arts and design', 'Artist, Designer, Illustrator, Creative Industry Roles', '3–4 Years', 'Medium'),
(31, 4, 'BHM', 'Bachelor of Hotel Management-Undergraduate program focused on hospitality and hotel administration.', 'Hotel Operations, Food & Beverage, Hospitality Management, Tourism', 'Students interested in hospitality, tourism, and service industry', 'Hotel Manager, Hospitality Executive, Tourism Industry Roles', '3–4 Years', 'Medium'),
(32, 5, 'BCA', 'Bachelor of Computer Applications - Undergraduate program focused on computer applications, software development, and programming.', 'Programming, Data Structures, Database Management, Web Technologies, Operating Systems', 'Students interested in software development, applications, and IT careers', 'Software Developer, Web Developer, IT Analyst, MCA, Tech Industry Roles', '3 Years', 'Medium'),
(33, 5, 'B.Sc (CS / IT)', 'Undergraduate program focused on computer science theory and information technology fundamentals.', 'Computer Science, Programming, Algorithms, Networking, Data Structures', 'Students interested in core computer science concepts and IT systems', 'Software Engineer, Data Analyst, IT Consultant, Higher Studies, Research', '3 Years', 'Medium'),
(34, 6, 'MA (Master of Arts)', 'Postgraduate program focused on advanced studies in arts, humanities, and social sciences.', 'Literature, History, Political Science, Sociology, Economics (specialization based)', 'Students interested in academics, research, teaching, and civil services', 'Lecturer, Researcher, Civil Services, Policy Analyst', '2 Years', 'Medium'),
(35, 6, 'M.Com', 'Master of Commerce - Postgraduate program focused on advanced commerce, accounting, finance, and business studies.', 'Advanced Accounting, Finance, Taxation, Economics, Business Management', 'Commerce graduates aiming for academics, finance, or corporate roles', 'Accountant, Finance Manager, Lecturer, Researcher', '2 Years', 'Medium'),
(36, 6, 'LLM (Master of Laws)', 'Postgraduate law program offering advanced legal education and specialization.', 'Constitutional Law, Corporate Law, Criminal Law, International Law', 'Law graduates aiming for legal specialization, academics, or judiciary', 'Legal Consultant, Law Professor, Judicial Services, Policy Advisor', '1–2 Years', 'Hard'),
(37, 7, 'Post-Doctoral Fellowship (Post-Doc)', 'Advanced research program undertaken after completion of a PhD.', 'Advanced Research, Publications, Specialized Studies', 'PhD holders aiming for advanced academic or global research roles', 'Senior Researcher, International Academic Positions', '1–3 Years', 'Hard'),
(38, 7, 'NET / SET / JRF Qualification', 'National-level eligibility qualification for teaching and funded research positions in universities and colleges.', 'Subject Knowledge, Research Aptitude, Teaching Aptitude', 'Postgraduates aiming for assistant professor or research fellow positions', 'Assistant Professor, Research Fellow (JRF), Academic Careers', '6–12 Months', 'Hard'),
(39, 7, 'Professional Doctorate (Doctor of Laws)', 'Practice-oriented doctoral programs focused on applied research and professional expertise.', 'Advanced Professional Studies, Applied Research, Case Studies', 'Professionals seeking doctoral-level expertise with industry or legal specialization', 'Senior Academic Roles, Industry-Academic Leadership, Legal Expertise', '3–5 Years', 'Hard');

-- --------------------------------------------------------

--
-- Table structure for table `streams_backup`
--

CREATE TABLE `streams_backup` (
  `stream_id` int(11) NOT NULL DEFAULT 0,
  `education_level_id` int(11) DEFAULT NULL,
  `stream_name` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `subjects` text DEFAULT NULL,
  `who_should_choose` text DEFAULT NULL,
  `career_scope` text DEFAULT NULL,
  `duration` varchar(50) DEFAULT NULL,
  `difficulty_level` enum('Easy','Medium','Hard') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `streams_backup`
--

INSERT INTO `streams_backup` (`stream_id`, `education_level_id`, `stream_name`, `description`, `subjects`, `who_should_choose`, `career_scope`, `duration`, `difficulty_level`) VALUES
(1, 1, 'Science Stream (PCM)', 'Academic science stream focusing on Physics, Chemistry and Mathematics.', 'Physics, Chemistry, Mathematics', 'Students interested in engineering, technology and problem-solving.', 'Engineering, Architecture, Defence, Research', '2 Years', 'Hard'),
(2, 1, 'Science Stream (PCB)', 'Science stream focusing on Biology along with Physics and Chemistry.', 'Physics, Chemistry, Biology', 'Students interested in medicine, healthcare and life sciences.', 'Medical, Pharmacy, Biotechnology, Research', '2 Years', 'Hard'),
(3, 1, 'Science Stream (PCMB)', 'Combined science stream including Maths and Biology.', 'Physics, Chemistry, Mathematics, Biology', 'Students who want flexibility between medical and engineering.', 'Medical, Engineering, Research', '2 Years', 'Hard'),
(4, 1, 'Commerce Stream', 'Business-oriented academic stream.', 'Accountancy, Business Studies, Economics', 'Students interested in business, finance and management.', 'CA, MBA, Banking, Corporate Jobs', '2 Years', 'Medium'),
(5, 1, 'Arts / Humanities Stream', 'Humanities-focused academic stream.', 'History, Political Science, Sociology, Economics', 'Students interested in social sciences, law and civil services.', 'Law, UPSC, Teaching, Media', '2 Years', 'Medium'),
(6, 1, 'Vocational / ITI', 'Skill-based vocational education.', 'Technical Skills, Trade Subjects', 'Students who prefer practical, job-oriented learning.', 'Technician, Skilled Trades, Industry Jobs', '1–2 Years', 'Easy'),
(7, 2, 'Engineering (B.Tech)', 'Undergraduate engineering degree.', 'Mathematics, Engineering Subjects', 'Students interested in technology and innovation.', 'Software, Core Engineering, Govt Jobs', '4 Years', 'Hard'),
(8, 2, 'Medical (MBBS / BDS)', 'Professional medical education.', 'Biology, Medicine', 'Students aiming to become doctors.', 'Doctor, Surgeon, Healthcare', '5–6 Years', 'Hard'),
(9, 2, 'Pure Sciences (B.Sc)', 'Academic science degree.', 'Physics, Chemistry, Maths, Biology', 'Students interested in research and teaching.', 'Research, Teaching, Higher Studies', '3 Years', 'Medium'),
(10, 2, 'Architecture (B.Arch)', 'Professional architecture degree.', 'Mathematics, Design, Architecture', 'Students interested in design and construction.', 'Architect, Urban Planner', '5 Years', 'Hard'),
(11, 2, 'Pharmacy (B.Pharm)', 'Pharmaceutical sciences degree.', 'Chemistry, Pharmacology', 'Students interested in medicines and drugs.', 'Pharmacist, Pharma Industry', '4 Years', 'Medium'),
(12, 2, 'Engineering (B.Tech)', 'Undergraduate engineering degree.', 'Mathematics, Engineering Subjects', 'Students interested in technology and innovation.', 'Software, Core Engineering, Govt Jobs', '4 Years', 'Hard'),
(13, 2, 'Medical (MBBS / BDS)', 'Professional medical education.', 'Biology, Medicine', 'Students aiming to become doctors.', 'Doctor, Surgeon, Healthcare', '5–6 Years', 'Hard'),
(14, 2, 'Pure Sciences (B.Sc)', 'Academic science degree.', 'Physics, Chemistry, Maths, Biology', 'Students interested in research and teaching.', 'Research, Teaching, Higher Studies', '3 Years', 'Medium'),
(15, 2, 'Architecture (B.Arch)', 'Professional architecture degree.', 'Mathematics, Design, Architecture', 'Students interested in design and construction.', 'Architect, Urban Planner', '5 Years', 'Hard'),
(16, 2, 'Pharmacy (B.Pharm)', 'Pharmaceutical sciences degree.', 'Chemistry, Pharmacology', 'Students interested in medicines and drugs.', 'Pharmacist, Pharma Industry', '4 Years', 'Medium'),
(17, 3, 'B.Com', 'Undergraduate commerce degree.', 'Accounting, Finance, Economics', 'Students interested in finance and accounting.', 'CA, MBA, Banking, Corporate', '3 Years', 'Medium'),
(18, 3, 'BBA', 'Business administration degree.', 'Management, Marketing, Finance', 'Students interested in management roles.', 'MBA, Corporate Management', '3 Years', 'Medium'),
(19, 3, 'Chartered Accountancy (CA)', 'Professional accounting qualification.', 'Accounting, Taxation, Auditing', 'Students aiming for professional accounting.', 'CA Practice, Corporate Finance', '4–5 Years', 'Hard'),
(20, 4, 'BA', 'Undergraduate arts degree.', 'History, Political Science, Economics', 'Students interested in humanities.', 'UPSC, Teaching, Research', '3 Years', 'Medium'),
(21, 4, 'Law (LLB)', 'Professional law degree.', 'Law Subjects', 'Students interested in legal careers.', 'Lawyer, Judiciary, Legal Advisor', '5 Years', 'Hard'),
(22, 4, 'Mass Communication', 'Media and communication studies.', 'Journalism, Media Studies', 'Students interested in media.', 'Journalist, Media Professional', '3 Years', 'Medium'),
(23, 5, 'B.Tech (Lateral Entry)', 'Engineering degree through lateral entry.', 'Engineering Subjects', 'Diploma holders continuing education.', 'Engineering Jobs', '3 Years', 'Hard'),
(24, 5, 'Advanced Diploma', 'Specialized diploma programs.', 'Technical Subjects', 'Students upgrading technical skills.', 'Supervisor, Specialist Roles', '1 Year', 'Medium'),
(25, 6, 'MBA', 'Postgraduate management degree.', 'Management, Finance, HR, Marketing', 'Students aiming for leadership roles.', 'Managerial & Corporate Jobs', '2 Years', 'Medium'),
(26, 6, 'M.Tech', 'Postgraduate engineering degree.', 'Advanced Engineering', 'Engineering graduates.', 'Research, Senior Engineer', '2 Years', 'Hard'),
(27, 6, 'M.Sc', 'Postgraduate science degree.', 'Advanced Science Subjects', 'Science graduates.', 'Research, Teaching', '2 Years', 'Medium'),
(28, 7, 'PhD', 'Doctoral research program.', 'Research Methodology', 'Students interested in deep research.', 'Professor, Scientist', '3–5 Years', 'Hard'),
(29, 7, 'Professional Certification', 'Industry-recognized certifications.', 'Domain-specific Skills', 'Professionals upgrading skills.', 'Senior Professional Roles', '6–12 Months', 'Easy');

-- --------------------------------------------------------

--
-- Table structure for table `streams_old`
--

CREATE TABLE `streams_old` (
  `stream_id` int(11) NOT NULL,
  `education_level_id` int(11) DEFAULT NULL,
  `stream_name` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `subjects` text DEFAULT NULL,
  `who_should_choose` text DEFAULT NULL,
  `career_scope` text DEFAULT NULL,
  `duration` varchar(50) DEFAULT NULL,
  `difficulty_level` enum('Easy','Medium','Hard') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `streams_old`
--

INSERT INTO `streams_old` (`stream_id`, `education_level_id`, `stream_name`, `description`, `subjects`, `who_should_choose`, `career_scope`, `duration`, `difficulty_level`) VALUES
(1, 1, 'Science Stream (PCM)', 'Academic science stream focusing on Physics, Chemistry and Mathematics.', 'Physics, Chemistry, Mathematics', 'Students interested in engineering, technology and problem-solving.', 'Engineering, Architecture, Defence, Research', '2 Years', 'Hard'),
(2, 1, 'Science Stream (PCB)', 'Science stream focusing on Biology along with Physics and Chemistry.', 'Physics, Chemistry, Biology', 'Students interested in medicine, healthcare and life sciences.', 'Medical, Pharmacy, Biotechnology, Research', '2 Years', 'Hard'),
(3, 1, 'Science Stream (PCMB)', 'Combined science stream including Maths and Biology.', 'Physics, Chemistry, Mathematics, Biology', 'Students who want flexibility between medical and engineering.', 'Medical, Engineering, Research', '2 Years', 'Hard'),
(4, 1, 'Commerce Stream', 'Business-oriented academic stream.', 'Accountancy, Business Studies, Economics', 'Students interested in business, finance and management.', 'CA, MBA, Banking, Corporate Jobs', '2 Years', 'Medium'),
(5, 1, 'Arts / Humanities Stream', 'Humanities-focused academic stream.', 'History, Political Science, Sociology, Economics', 'Students interested in social sciences, law and civil services.', 'Law, UPSC, Teaching, Media', '2 Years', 'Medium'),
(6, 1, 'Vocational / ITI', 'Skill-based vocational education.', 'Technical Skills, Trade Subjects', 'Students who prefer practical, job-oriented learning.', 'Technician, Skilled Trades, Industry Jobs', '1–2 Years', 'Easy'),
(7, 2, 'Engineering (B.Tech)', 'Undergraduate engineering degree.', 'Mathematics, Engineering Subjects', 'Students interested in technology and innovation.', 'Software, Core Engineering, Govt Jobs', '4 Years', 'Hard'),
(8, 2, 'Medical (MBBS / BDS)', 'Professional medical education.', 'Biology, Medicine', 'Students aiming to become doctors.', 'Doctor, Surgeon, Healthcare', '5–6 Years', 'Hard'),
(9, 2, 'Pure Sciences (B.Sc)', 'Academic science degree.', 'Physics, Chemistry, Maths, Biology', 'Students interested in research and teaching.', 'Research, Teaching, Higher Studies', '3 Years', 'Medium'),
(10, 2, 'Architecture (B.Arch)', 'Professional architecture degree.', 'Mathematics, Design, Architecture', 'Students interested in design and construction.', 'Architect, Urban Planner', '5 Years', 'Hard'),
(11, 2, 'Pharmacy (B.Pharm)', 'Pharmaceutical sciences degree.', 'Chemistry, Pharmacology', 'Students interested in medicines and drugs.', 'Pharmacist, Pharma Industry', '4 Years', 'Medium'),
(12, 2, 'Engineering (B.Tech)', 'Undergraduate engineering degree.', 'Mathematics, Engineering Subjects', 'Students interested in technology and innovation.', 'Software, Core Engineering, Govt Jobs', '4 Years', 'Hard'),
(13, 2, 'Medical (MBBS / BDS)', 'Professional medical education.', 'Biology, Medicine', 'Students aiming to become doctors.', 'Doctor, Surgeon, Healthcare', '5–6 Years', 'Hard'),
(14, 2, 'Pure Sciences (B.Sc)', 'Academic science degree.', 'Physics, Chemistry, Maths, Biology', 'Students interested in research and teaching.', 'Research, Teaching, Higher Studies', '3 Years', 'Medium'),
(15, 2, 'Architecture (B.Arch)', 'Professional architecture degree.', 'Mathematics, Design, Architecture', 'Students interested in design and construction.', 'Architect, Urban Planner', '5 Years', 'Hard'),
(16, 2, 'Pharmacy (B.Pharm)', 'Pharmaceutical sciences degree.', 'Chemistry, Pharmacology', 'Students interested in medicines and drugs.', 'Pharmacist, Pharma Industry', '4 Years', 'Medium'),
(17, 3, 'B.Com', 'Undergraduate commerce degree.', 'Accounting, Finance, Economics', 'Students interested in finance and accounting.', 'CA, MBA, Banking, Corporate', '3 Years', 'Medium'),
(18, 3, 'BBA', 'Business administration degree.', 'Management, Marketing, Finance', 'Students interested in management roles.', 'MBA, Corporate Management', '3 Years', 'Medium'),
(19, 3, 'Chartered Accountancy (CA)', 'Professional accounting qualification.', 'Accounting, Taxation, Auditing', 'Students aiming for professional accounting.', 'CA Practice, Corporate Finance', '4–5 Years', 'Hard'),
(20, 4, 'BA', 'Undergraduate arts degree.', 'History, Political Science, Economics', 'Students interested in humanities.', 'UPSC, Teaching, Research', '3 Years', 'Medium'),
(21, 4, 'Law (LLB)', 'Professional law degree.', 'Law Subjects', 'Students interested in legal careers.', 'Lawyer, Judiciary, Legal Advisor', '5 Years', 'Hard'),
(22, 4, 'Mass Communication', 'Media and communication studies.', 'Journalism, Media Studies', 'Students interested in media.', 'Journalist, Media Professional', '3 Years', 'Medium'),
(23, 5, 'B.Tech (Lateral Entry)', 'Engineering degree through lateral entry.', 'Engineering Subjects', 'Diploma holders continuing education.', 'Engineering Jobs', '3 Years', 'Hard'),
(24, 5, 'Advanced Diploma', 'Specialized diploma programs.', 'Technical Subjects', 'Students upgrading technical skills.', 'Supervisor, Specialist Roles', '1 Year', 'Medium'),
(25, 6, 'MBA', 'Postgraduate management degree.', 'Management, Finance, HR, Marketing', 'Students aiming for leadership roles.', 'Managerial & Corporate Jobs', '2 Years', 'Medium'),
(26, 6, 'M.Tech', 'Postgraduate engineering degree.', 'Advanced Engineering', 'Engineering graduates.', 'Research, Senior Engineer', '2 Years', 'Hard'),
(27, 6, 'M.Sc', 'Postgraduate science degree.', 'Advanced Science Subjects', 'Science graduates.', 'Research, Teaching', '2 Years', 'Medium'),
(28, 7, 'PhD', 'Doctoral research program.', 'Research Methodology', 'Students interested in deep research.', 'Professor, Scientist', '3–5 Years', 'Hard'),
(29, 7, 'Professional Certification', 'Industry-recognized certifications.', 'Domain-specific Skills', 'Professionals upgrading skills.', 'Senior Professional Roles', '6–12 Months', 'Easy');

-- --------------------------------------------------------

--
-- Table structure for table `stream_exams`
--

CREATE TABLE `stream_exams` (
  `id` int(11) NOT NULL,
  `stream_id` int(11) DEFAULT NULL,
  `exam_id` int(11) DEFAULT NULL,
  `exam_role` enum('MANDATORY','OPTIONAL','SCHOLARSHIP') DEFAULT 'MANDATORY'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stream_exams`
--

INSERT INTO `stream_exams` (`id`, `stream_id`, `exam_id`, `exam_role`) VALUES
(39, 1, 3, 'OPTIONAL'),
(40, 2, 3, 'OPTIONAL'),
(41, 3, 3, 'OPTIONAL'),
(42, 6, 2, 'MANDATORY'),
(43, 7, 4, 'MANDATORY'),
(44, 7, 5, 'OPTIONAL'),
(45, 7, 7, 'OPTIONAL'),
(46, 7, 22, 'OPTIONAL'),
(47, 8, 6, 'MANDATORY'),
(48, 9, 9, 'MANDATORY'),
(49, 9, 22, 'OPTIONAL'),
(50, 10, 8, 'MANDATORY'),
(51, 11, 6, 'MANDATORY'),
(52, 11, 22, 'OPTIONAL'),
(54, 12, 20, 'OPTIONAL'),
(55, 13, 10, 'OPTIONAL'),
(56, 13, 20, 'OPTIONAL'),
(58, 14, 11, 'MANDATORY'),
(59, 15, 13, 'MANDATORY'),
(60, 16, 12, 'MANDATORY'),
(61, 17, 13, 'OPTIONAL'),
(62, 19, 1, 'MANDATORY'),
(63, 20, 14, 'MANDATORY'),
(64, 21, 16, 'MANDATORY'),
(65, 21, 34, 'OPTIONAL'),
(66, 22, 15, 'MANDATORY'),
(67, 23, 23, 'MANDATORY'),
(68, 24, 26, 'MANDATORY'),
(69, 24, 27, 'OPTIONAL'),
(70, 24, 29, 'MANDATORY'),
(72, 27, 18, 'MANDATORY'),
(73, 28, 19, 'MANDATORY'),
(74, 29, 13, 'OPTIONAL'),
(75, 30, 13, 'OPTIONAL'),
(76, 31, 21, 'MANDATORY'),
(77, 32, 9, 'OPTIONAL'),
(78, 33, 9, 'OPTIONAL'),
(79, 34, 23, 'MANDATORY'),
(80, 35, 23, 'MANDATORY'),
(81, 36, 25, 'MANDATORY'),
(82, 12, 31, 'MANDATORY'),
(83, 13, 31, 'OPTIONAL'),
(84, 26, 31, 'OPTIONAL'),
(85, 30, 33, 'MANDATORY'),
(94, 23, 24, 'OPTIONAL'),
(95, 34, 24, 'OPTIONAL'),
(96, 35, 24, 'OPTIONAL'),
(97, 36, 24, 'OPTIONAL'),
(98, 24, 28, 'OPTIONAL');

-- --------------------------------------------------------

--
-- Table structure for table `stream_jobs`
--

CREATE TABLE `stream_jobs` (
  `id` int(11) NOT NULL,
  `stream_id` int(11) DEFAULT NULL,
  `job_id` int(11) DEFAULT NULL,
  `eligibility_strength` enum('PRIMARY','SECONDARY','INVALID') DEFAULT 'PRIMARY'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stream_jobs`
--

INSERT INTO `stream_jobs` (`id`, `stream_id`, `job_id`, `eligibility_strength`) VALUES
(106, 1, 53, 'PRIMARY'),
(107, 1, 54, 'PRIMARY'),
(108, 1, 56, 'PRIMARY'),
(109, 1, 57, 'PRIMARY'),
(110, 1, 73, 'PRIMARY'),
(111, 1, 74, 'SECONDARY'),
(112, 2, 75, 'PRIMARY'),
(113, 2, 76, 'PRIMARY'),
(114, 2, 53, 'PRIMARY'),
(115, 2, 77, 'PRIMARY'),
(116, 2, 78, 'SECONDARY'),
(117, 2, 57, 'SECONDARY'),
(118, 3, 64, 'PRIMARY'),
(119, 3, 65, 'PRIMARY'),
(120, 3, 53, 'PRIMARY'),
(121, 3, 73, 'PRIMARY'),
(122, 3, 58, 'SECONDARY'),
(123, 3, 57, 'SECONDARY'),
(124, 4, 98, 'PRIMARY'),
(125, 4, 62, 'PRIMARY'),
(126, 4, 61, 'PRIMARY'),
(127, 4, 66, 'PRIMARY'),
(128, 4, 58, 'PRIMARY'),
(129, 4, 59, 'SECONDARY'),
(130, 5, 98, 'PRIMARY'),
(131, 5, 53, 'PRIMARY'),
(132, 5, 56, 'PRIMARY'),
(133, 5, 69, 'PRIMARY'),
(134, 5, 67, 'PRIMARY'),
(135, 5, 70, 'SECONDARY'),
(136, 6, 97, 'PRIMARY'),
(137, 6, 54, 'PRIMARY'),
(138, 6, 42, 'SECONDARY'),
(139, 6, 60, 'PRIMARY'),
(140, 6, 73, 'PRIMARY'),
(141, 6, 58, 'SECONDARY'),
(142, 7, 83, 'PRIMARY'),
(143, 7, 84, 'PRIMARY'),
(144, 7, 97, 'SECONDARY'),
(145, 7, 1, 'PRIMARY'),
(146, 7, 85, 'PRIMARY'),
(147, 7, 86, 'PRIMARY'),
(148, 8, 79, 'PRIMARY'),
(149, 8, 80, 'PRIMARY'),
(150, 8, 76, 'SECONDARY'),
(151, 8, 5, 'PRIMARY'),
(152, 8, 81, 'PRIMARY'),
(153, 8, 82, 'SECONDARY'),
(154, 9, 75, 'PRIMARY'),
(155, 9, 97, 'PRIMARY'),
(156, 9, 13, 'SECONDARY'),
(157, 9, 14, 'PRIMARY'),
(158, 9, 2, 'SECONDARY'),
(159, 9, 78, 'SECONDARY'),
(160, 10, 83, 'PRIMARY'),
(161, 10, 3, 'SECONDARY'),
(162, 10, 31, 'SECONDARY'),
(163, 10, 86, 'PRIMARY'),
(164, 10, 74, 'PRIMARY'),
(165, 10, 85, 'SECONDARY'),
(166, 11, 75, 'PRIMARY'),
(167, 11, 76, 'PRIMARY'),
(168, 11, 79, 'SECONDARY'),
(169, 11, 6, 'PRIMARY'),
(170, 11, 77, 'PRIMARY'),
(171, 11, 82, 'SECONDARY'),
(172, 12, 95, 'PRIMARY'),
(173, 12, 96, 'PRIMARY'),
(174, 12, 29, 'PRIMARY'),
(175, 12, 88, 'PRIMARY'),
(176, 12, 90, 'PRIMARY'),
(177, 12, 50, 'SECONDARY'),
(178, 13, 8, 'PRIMARY'),
(179, 13, 31, 'PRIMARY'),
(180, 13, 98, 'SECONDARY'),
(181, 13, 9, 'PRIMARY'),
(182, 13, 16, 'PRIMARY'),
(183, 13, 17, 'PRIMARY'),
(184, 14, 87, 'PRIMARY'),
(185, 14, 29, 'PRIMARY'),
(186, 14, 28, 'SECONDARY'),
(187, 14, 7, 'PRIMARY'),
(188, 14, 22, 'PRIMARY'),
(189, 14, 23, 'SECONDARY'),
(190, 15, 91, 'PRIMARY'),
(191, 15, 92, 'PRIMARY'),
(192, 15, 98, 'PRIMARY'),
(193, 15, 93, 'PRIMARY'),
(194, 15, 94, 'PRIMARY'),
(195, 15, 47, 'SECONDARY'),
(196, 16, 52, 'PRIMARY'),
(197, 16, 51, 'PRIMARY'),
(198, 16, 27, 'SECONDARY'),
(199, 16, 11, 'PRIMARY'),
(200, 16, 20, 'PRIMARY'),
(201, 16, 19, 'SECONDARY'),
(202, 17, 33, 'PRIMARY'),
(203, 17, 31, 'SECONDARY'),
(204, 17, 98, 'SECONDARY'),
(205, 17, 12, 'PRIMARY'),
(206, 17, 47, 'PRIMARY'),
(207, 17, 48, 'PRIMARY'),
(208, 19, 42, 'SECONDARY'),
(209, 19, 54, 'PRIMARY'),
(210, 19, 97, 'PRIMARY'),
(211, 19, 60, 'PRIMARY'),
(212, 19, 73, 'PRIMARY'),
(213, 19, 58, 'SECONDARY'),
(214, 20, 83, 'PRIMARY'),
(215, 20, 84, 'PRIMARY'),
(216, 20, 97, 'SECONDARY'),
(217, 20, 1, 'PRIMARY'),
(218, 20, 85, 'PRIMARY'),
(219, 20, 86, 'PRIMARY'),
(220, 21, 31, 'PRIMARY'),
(221, 21, 8, 'SECONDARY'),
(222, 21, 10, 'SECONDARY'),
(223, 21, 9, 'PRIMARY'),
(224, 21, 23, 'PRIMARY'),
(225, 21, 15, 'PRIMARY'),
(226, 22, 84, 'PRIMARY'),
(227, 22, 83, 'PRIMARY'),
(228, 22, 43, 'SECONDARY'),
(229, 22, 85, 'PRIMARY'),
(230, 22, 86, 'PRIMARY'),
(231, 22, 1, 'SECONDARY'),
(232, 23, 13, 'SECONDARY'),
(233, 23, 14, 'PRIMARY'),
(234, 23, 75, 'PRIMARY'),
(235, 23, 78, 'SECONDARY'),
(236, 23, 2, 'SECONDARY'),
(237, 23, 82, 'SECONDARY'),
(238, 24, 13, 'PRIMARY'),
(239, 24, 14, 'PRIMARY'),
(240, 24, 84, 'SECONDARY'),
(241, 24, 93, 'SECONDARY'),
(242, 24, 48, 'SECONDARY'),
(243, 24, 31, 'SECONDARY'),
(244, 26, 31, 'PRIMARY'),
(245, 26, 8, 'PRIMARY'),
(246, 26, 98, 'SECONDARY'),
(247, 26, 9, 'PRIMARY'),
(248, 26, 16, 'PRIMARY'),
(249, 26, 17, 'PRIMARY'),
(250, 27, 26, 'PRIMARY'),
(251, 27, 19, 'PRIMARY'),
(252, 27, 51, 'SECONDARY'),
(253, 27, 18, 'PRIMARY'),
(254, 27, 20, 'SECONDARY'),
(255, 27, 50, 'SECONDARY'),
(256, 28, 28, 'PRIMARY'),
(257, 28, 29, 'PRIMARY'),
(258, 28, 95, 'SECONDARY'),
(259, 28, 21, 'PRIMARY'),
(260, 28, 22, 'SECONDARY'),
(261, 28, 23, 'SECONDARY'),
(262, 29, 31, 'PRIMARY'),
(263, 29, 8, 'SECONDARY'),
(264, 29, 98, 'SECONDARY'),
(265, 29, 9, 'PRIMARY'),
(266, 29, 16, 'PRIMARY'),
(267, 29, 15, 'PRIMARY'),
(268, 30, 34, 'PRIMARY'),
(269, 30, 33, 'SECONDARY'),
(270, 30, 31, 'SECONDARY'),
(271, 30, 40, 'PRIMARY'),
(272, 30, 41, 'PRIMARY'),
(273, 30, 47, 'SECONDARY'),
(274, 31, 38, 'PRIMARY'),
(275, 31, 39, 'PRIMARY'),
(276, 31, 31, 'SECONDARY'),
(277, 31, 35, 'PRIMARY'),
(278, 31, 36, 'PRIMARY'),
(279, 31, 37, 'PRIMARY'),
(280, 32, 42, 'PRIMARY'),
(281, 32, 43, 'PRIMARY'),
(282, 32, 97, 'SECONDARY'),
(283, 32, 1, 'PRIMARY'),
(284, 32, 2, 'PRIMARY'),
(285, 32, 85, 'SECONDARY'),
(286, 33, 45, 'PRIMARY'),
(287, 33, 42, 'PRIMARY'),
(288, 33, 97, 'SECONDARY'),
(289, 33, 1, 'PRIMARY'),
(290, 33, 2, 'PRIMARY'),
(291, 33, 85, 'SECONDARY'),
(292, 34, 33, 'PRIMARY'),
(293, 34, 91, 'PRIMARY'),
(294, 34, 92, 'PRIMARY'),
(295, 34, 47, 'PRIMARY'),
(296, 34, 93, 'SECONDARY'),
(297, 34, 48, 'SECONDARY'),
(298, 35, 29, 'PRIMARY'),
(299, 35, 95, 'PRIMARY'),
(300, 35, 96, 'SECONDARY'),
(301, 35, 50, 'PRIMARY'),
(302, 35, 49, 'PRIMARY'),
(303, 35, 23, 'SECONDARY'),
(304, 36, 51, 'PRIMARY'),
(305, 36, 52, 'PRIMARY'),
(306, 36, 27, 'SECONDARY'),
(307, 36, 20, 'PRIMARY'),
(308, 36, 19, 'SECONDARY'),
(309, 36, 11, 'SECONDARY'),
(310, 37, 14, 'PRIMARY'),
(311, 37, 13, 'PRIMARY'),
(312, 37, 84, 'SECONDARY'),
(313, 37, 93, 'SECONDARY'),
(314, 37, 48, 'SECONDARY'),
(315, 37, 31, 'SECONDARY'),
(316, 25, 31, 'SECONDARY'),
(317, 25, 46, 'SECONDARY'),
(318, 25, 84, 'SECONDARY'),
(319, 25, 23, 'PRIMARY'),
(320, 25, 50, 'PRIMARY'),
(321, 25, 93, 'PRIMARY'),
(322, 25, 86, 'PRIMARY'),
(323, 25, 85, 'PRIMARY');

-- --------------------------------------------------------

--
-- Table structure for table `stream_progression`
--

CREATE TABLE `stream_progression` (
  `id` int(11) NOT NULL,
  `current_stream_id` int(11) DEFAULT NULL,
  `next_stream_id` int(11) DEFAULT NULL,
  `progression_type` enum('ACADEMIC','OPTIONAL','INVALID') DEFAULT 'ACADEMIC'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stream_progression`
--

INSERT INTO `stream_progression` (`id`, `current_stream_id`, `next_stream_id`, `progression_type`) VALUES
(1, 1, 7, 'ACADEMIC'),
(2, 1, 9, 'ACADEMIC'),
(3, 1, 10, 'ACADEMIC'),
(4, 2, 8, 'ACADEMIC'),
(5, 2, 9, 'ACADEMIC'),
(6, 2, 11, 'ACADEMIC'),
(7, 3, 7, 'ACADEMIC'),
(8, 3, 8, 'ACADEMIC'),
(9, 3, 9, 'ACADEMIC'),
(10, 4, 12, 'ACADEMIC'),
(11, 4, 13, 'ACADEMIC'),
(12, 4, 14, 'ACADEMIC'),
(13, 5, 15, 'ACADEMIC'),
(14, 5, 16, 'ACADEMIC'),
(15, 5, 17, 'ACADEMIC'),
(16, 6, 19, 'ACADEMIC'),
(17, 7, 22, 'ACADEMIC'),
(18, 7, 21, 'ACADEMIC'),
(19, 8, 23, 'ACADEMIC'),
(20, 9, 23, 'ACADEMIC'),
(21, 9, 32, 'ACADEMIC'),
(22, 9, 33, 'ACADEMIC'),
(23, 10, 22, 'ACADEMIC'),
(24, 11, 22, 'ACADEMIC'),
(25, 12, 21, 'ACADEMIC'),
(26, 12, 35, 'ACADEMIC'),
(27, 13, 21, 'ACADEMIC'),
(28, 14, 21, 'ACADEMIC'),
(29, 26, 21, 'ACADEMIC'),
(30, 27, 21, 'ACADEMIC'),
(31, 28, 21, 'ACADEMIC'),
(32, 12, 16, 'ACADEMIC'),
(33, 15, 21, 'ACADEMIC'),
(34, 16, 36, 'ACADEMIC'),
(35, 17, 21, 'ACADEMIC'),
(36, 29, 21, 'ACADEMIC'),
(38, 31, 21, 'ACADEMIC'),
(39, 15, 35, 'ACADEMIC'),
(40, 19, 20, 'ACADEMIC'),
(41, 19, 32, 'ACADEMIC'),
(42, 19, 33, 'ACADEMIC'),
(43, 20, 22, 'ACADEMIC'),
(45, 33, 23, 'ACADEMIC'),
(49, 9, 21, 'ACADEMIC'),
(52, 32, 21, 'ACADEMIC'),
(53, 33, 21, 'ACADEMIC'),
(54, 21, 24, 'ACADEMIC'),
(55, 22, 24, 'ACADEMIC'),
(56, 23, 24, 'ACADEMIC'),
(57, 34, 24, 'ACADEMIC'),
(58, 35, 24, 'ACADEMIC'),
(59, 36, 24, 'ACADEMIC'),
(60, 34, 38, 'ACADEMIC'),
(61, 35, 38, 'ACADEMIC'),
(62, 23, 38, 'ACADEMIC'),
(63, 24, 37, 'ACADEMIC'),
(68, 25, 24, 'ACADEMIC');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `auth_token` varchar(255) DEFAULT NULL,
  `auth_provider` enum('EMAIL','GOOGLE') DEFAULT 'EMAIL',
  `education_level_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `phone` varchar(20) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `education_level` varchar(100) DEFAULT NULL,
  `current_school` varchar(255) DEFAULT NULL,
  `board` varchar(100) DEFAULT NULL,
  `last_exam_score` varchar(50) DEFAULT NULL,
  `aspiring_career` varchar(255) DEFAULT NULL,
  `profile_image` varchar(500) DEFAULT NULL,
  `reset_token` varchar(64) DEFAULT NULL,
  `reset_token_expires` datetime DEFAULT NULL,
  `fcm_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `full_name`, `email`, `password_hash`, `auth_token`, `auth_provider`, `education_level_id`, `created_at`, `phone`, `date_of_birth`, `education_level`, `current_school`, `board`, `last_exam_score`, `aspiring_career`, `profile_image`, `reset_token`, `reset_token_expires`, `fcm_token`) VALUES
(1, 'Test User', 'testuser@gmail.com', '$2y$10$0Ysw4obo2VjtvufJdAeTj..CM8cEpu5lYFwoSXSN0C2fYlMrqUeQO', '0ad4b4e78b3bf5e5dc3fb81e0612e9c69033533a3659675a57d42a2c02721092', 'EMAIL', NULL, '2025-12-26 09:29:07', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 'User', 'user@gmail.com', '$2y$10$BLFvzevTJPuhQHrvkIR4B.rT8DH/ALMBetvLZzLciwj81PSKtp9w2', '8edc7f3ef2cae5938c4ed400eee9578bc162caddd14cdf5a29dbd08447812a1e', 'EMAIL', NULL, '2025-12-26 09:35:26', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(3, 'Harshu', 'harshini@gmail.com', 'welcome', NULL, 'EMAIL', NULL, '2025-12-26 12:45:55', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(4, 'Siddu', 'siddu@example.com', '$2y$10$ulznk.XeqLuh535w6mZQFuwyuw1CCTEwpewjOYDR16XYdHKmH2QrS', 'c834c84e61e1231cf6858025c36b31eca9d5e2f708cdf30cf45d8360a18c13f3', 'EMAIL', NULL, '2025-12-26 13:01:11', '9133333337', '2004-12-29', 'Undergraduate', 'SIMATS ENGINEERING COLLEGE', 'State', '', 'Software Engineer', NULL, NULL, NULL, NULL),
(5, 'Siva', 'siva@gmail.com', '$2y$10$qI8Dk0yXeVuDKMJ94ZQ5n.2AoXnBV/iVnw1e6knuaDbEhlyKs27YW', 'e5d14eeed19c19aaee63f171538a1c91b7049c18c71501a7bc24346f6ff51a67', 'EMAIL', NULL, '2025-12-26 13:18:49', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(6, 'Harshu', 'harsh@gmail.com', '$2y$10$kGG2jsfG2Zu/lYFbXFu70e6i88FTdrjTXBEBDcF73o57GvJtQIe9e', '9616fffbf431462e32ba823ede4ec6d02f8c9bd5a14bbd1d268eb809db43794c', 'EMAIL', NULL, '2025-12-29 09:17:42', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(7, 'Harshini', 'harshu@gmail.com', '$2y$10$MeeKcd1NgooCJZJxAVx18eOJKoKC3TV8rj2lLU12aXxnACr5HNTSK', '2b2773ed3c6aa6fd91d80ac5a88c784314c5f9249f6d489259036bdc74abc18e', 'EMAIL', NULL, '2025-12-30 16:27:41', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(8, 'Siva', 'sivanehagunji@gmail.com', '$2y$10$tQsPtHmofrJDiwQkFsPvp.KmcEPjFSGRd.jtDep7FZFLVaUTM3.ka', '855f0f9645c51df25228b428c31d099e3315e5dc7f198bc8894f2fffc5dffbda', 'EMAIL', NULL, '2025-12-31 10:22:56', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'abb17e4f207eb9c3b91ea1db5d837244d6ea25236e50c7ba1b6b6c7b1d419718', '2025-12-31 12:35:19', NULL),
(9, 'Suma', 'sumathigunji8008@gmail.com', '$2y$10$yrxDiwqGNdLzhQjGlqcheO3/jL0DuEpYZViBHtfUuow78zxwnktnO', '1f98055e351a99dd7e1291e1d86bd26e72770a3035523b5e03a2173868d91768', 'EMAIL', NULL, '2025-12-31 10:49:45', '', NULL, '', '', '', '', '', 'uploads/profile_9_1767426540.jpg', NULL, NULL, 'fxh3yHGxRaelVwy1mKC-_p:APA91bFMVDYDUG6suC_AbRJduPRjnqiFCk4TX74X5uCNsc5SqEo78J7TOGzdCNCDkJ3ZkPD41tBE3GcVuTT4KO_hinn8D0qlXLKx1R89pCAcQhWBVzRh3s4'),
(10, 'Siddartha', 'siddarthareddy67@gmail.com', '$2y$10$XkHUsSwGoxTb3HUpOg771uoQai6q.HhdOVTs72hZI.hbmRva/aEcm', '0bebd595ba06a2ccd55df103842030b25925e1483c2da2b5c9c7aabc0e183eb5', 'EMAIL', NULL, '2025-12-31 11:10:56', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(11, 'Harshu', 'harshinigunji8008@gmail.com', '$2y$10$zP7.oTRwuDpUe.LJThzbse95S6FI0rXjNp3Z/i1ObGDcLxLUtw1ay', '8921ed007f12dd3b8d342ed18fa0e766fa21e072b4675c90bfcb9f02124f91b8', 'EMAIL', NULL, '2026-01-02 11:49:14', '', NULL, '', '', '', '', '', 'uploads/profile_11_1767435026.jpg', NULL, NULL, 'fxh3yHGxRaelVwy1mKC-_p:APA91bFMVDYDUG6suC_AbRJduPRjnqiFCk4TX74X5uCNsc5SqEo78J7TOGzdCNCDkJ3ZkPD41tBE3GcVuTT4KO_hinn8D0qlXLKx1R89pCAcQhWBVzRh3s4'),
(12, 'Harshini', 'harshinigunji@gmail.com', '$2y$10$G0X0FbmFAK.Ddx0SYZeiTegCYpKIzsdbKYGNi/ESI2s6o.EjvlRZK', 'd914f860dbbd1d67b59a3c0270eccd9fe7fda2b27a811bc3e2d8939681093cbb', 'EMAIL', NULL, '2026-01-02 12:00:03', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'eaPO-niNSSyt0LUD8C9R9H:APA91bEDvSry94yJJkOglmAl3-imEt09uPUeD7UjTKT-SyuhW8oFuSnUdkBm7tNzYzGs5pD-NWyWgQY33zxH2eSOReOA73eqIXJWAE9WNPZvz5mGVUXxEgk'),
(13, 'Akhil', 'akhilgunji@gmail.com', '$2y$10$xck0y0Ir3FUL/l/D6hw/Auq4z7h6C/vehlglQ90q44.ClTvcTKGWe', '1630f04d934e9c3c9d0ea0c8c31e7e3868404b1a15e37a0fec6fece56c644b7c', 'EMAIL', NULL, '2026-01-02 12:14:07', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'eaPO-niNSSyt0LUD8C9R9H:APA91bEDvSry94yJJkOglmAl3-imEt09uPUeD7UjTKT-SyuhW8oFuSnUdkBm7tNzYzGs5pD-NWyWgQY33zxH2eSOReOA73eqIXJWAE9WNPZvz5mGVUXxEgk');

-- --------------------------------------------------------

--
-- Table structure for table `user_roadmaps`
--

CREATE TABLE `user_roadmaps` (
  `roadmap_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `target_job_id` int(11) DEFAULT NULL,
  `target_job_name` varchar(255) DEFAULT NULL,
  `target_salary` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_roadmaps`
--

INSERT INTO `user_roadmaps` (`roadmap_id`, `user_id`, `title`, `target_job_id`, `target_job_name`, `target_salary`, `created_at`) VALUES
(1, 4, 'Science (PCM) to Software Engineer', NULL, 'Software Engineer', 'Competitive Salary', '2025-12-28 11:09:49'),
(2, 4, 'PhD to Research Scientist', NULL, 'Research Scientist', 'Competitive Salary', '2025-12-28 11:11:21'),
(3, 4, 'Science (PCM) to Bank Probationary Officer', NULL, 'Bank Probationary Officer', 'Competitive Salary', '2025-12-28 11:33:41'),
(4, 4, 'Science (PCM) to Research Scientist', NULL, 'Research Scientist', 'Competitive Salary', '2025-12-28 11:35:12'),
(5, 4, 'Science (PCM) to Mechanical Engineer', NULL, 'Mechanical Engineer', 'Competitive Salary', '2025-12-28 11:55:20'),
(6, 4, 'Science (PCM) to Software Engineer', NULL, 'Software Engineer', 'Competitive Salary', '2025-12-28 12:52:06'),
(7, 4, 'Science (PCM) to Software Engineer', NULL, 'Software Engineer', 'Competitive Salary', '2025-12-28 12:52:32'),
(8, 4, 'Science (PCB) to Research Scientist', NULL, 'Research Scientist', 'Competitive Salary', '2025-12-28 12:53:21'),
(9, 4, 'Science (PCB) to Doctor (MBBS)', NULL, 'Doctor (MBBS)', 'Competitive Salary', '2025-12-28 12:54:36'),
(10, 4, 'Science (PCM) to Mechanical Engineer', NULL, 'Mechanical Engineer', 'Competitive Salary', '2025-12-28 12:55:27'),
(11, 4, 'Science (PCM) to Software Engineer', NULL, 'Software Engineer', 'Competitive Salary', '2025-12-28 12:56:24'),
(12, 4, 'Science (PCM) to Mechanical Engineer', NULL, 'Mechanical Engineer', 'Competitive Salary', '2025-12-28 12:57:33'),
(13, 4, 'Science (PCM) to Research Scientist', NULL, 'Research Scientist', 'Competitive Salary', '2025-12-28 12:59:29'),
(14, 4, 'Science (PCB) to Doctor (MBBS)', NULL, 'Doctor (MBBS)', 'Competitive Salary', '2025-12-28 13:04:47'),
(15, 4, 'Engineering (B.Tech) to Mechanical Engineer', NULL, 'Mechanical Engineer', 'Competitive Salary', '2025-12-28 13:10:35'),
(16, 4, 'Pharmacy (B.Pharm) to Pharmacist', NULL, 'Pharmacist', 'Competitive Salary', '2025-12-28 13:11:11'),
(17, 4, 'MBA to Bank Probationary Officer', NULL, 'Bank Probationary Officer', 'Competitive Salary', '2025-12-28 13:12:44'),
(18, 4, 'PhD to Professor', NULL, 'Professor', 'Competitive Salary', '2025-12-28 13:13:15'),
(19, 4, 'Science (PCM) to Civil Engineer', NULL, 'Civil Engineer', 'Competitive Salary', '2025-12-28 17:59:29'),
(20, 4, '', NULL, 'Civil Engineer', '6–10 LPA', '2025-12-28 18:38:37'),
(21, 4, '', NULL, 'Civil Engineer', '6–10 LPA', '2025-12-28 18:38:39'),
(22, 4, '', NULL, 'Civil Engineer', '6–10 LPA', '2025-12-28 19:05:47'),
(23, 4, '', NULL, 'Civil Engineer', '6–10 LPA', '2025-12-28 19:05:51'),
(24, 4, 'Career Path to Doctor (MBBS)', NULL, 'Doctor (MBBS)', '8–20 LPA', '2025-12-28 19:08:45'),
(25, 4, 'Career Path to Doctor (MBBS)', NULL, 'Doctor (MBBS)', '8–20 LPA', '2025-12-28 19:08:51'),
(26, 4, 'Career Path to Doctor (MBBS)', NULL, 'Doctor (MBBS)', '8–20 LPA', '2025-12-28 19:09:38'),
(27, 4, 'Science (PCM) to Mechanical Engineer', NULL, 'Mechanical Engineer', 'Competitive Salary', '2025-12-29 04:01:09'),
(28, 4, 'Vocational / ITI to Mechanical Engineer', NULL, 'Mechanical Engineer', 'Competitive Salary', '2025-12-29 04:09:28'),
(29, 4, 'Vocational / ITI to Business Analyst', NULL, 'Business Analyst', 'Competitive Salary', '2025-12-29 04:10:23'),
(30, 4, 'B.Tech (Lateral Entry) to Mechanical Engineer', NULL, 'Mechanical Engineer', 'Competitive Salary', '2025-12-29 04:11:37'),
(31, 4, 'Advanced Diploma to Mechanical Engineer', NULL, 'Mechanical Engineer', 'Competitive Salary', '2025-12-29 04:13:30'),
(32, 4, 'Medical (MBBS / BDS) to Doctor (MBBS)', NULL, 'Doctor (MBBS)', 'Competitive Salary', '2025-12-29 04:19:21'),
(33, 4, 'BBA to Business Analyst', NULL, 'Business Analyst', 'Competitive Salary', '2025-12-29 04:20:49'),
(34, 4, 'Science (PCM) to Mechanical Engineer', NULL, 'Mechanical Engineer', 'Competitive Salary', '2025-12-29 04:22:09'),
(35, 4, 'Professional Certification to Data Analyst', NULL, 'Data Analyst', 'Competitive Salary', '2025-12-29 04:24:10'),
(36, 6, 'Science (PCM) to Software Engineer', NULL, 'Software Engineer', 'Competitive Salary', '2025-12-29 09:19:13'),
(37, 6, 'Science (PCM) to Civil Engineer', NULL, 'Civil Engineer', 'Competitive Salary', '2025-12-30 06:54:09'),
(38, 7, 'BA to Journalist', NULL, 'Journalist', 'Competitive Salary', '2025-12-30 16:29:49'),
(39, 7, 'Law (LLB) to Civil Services Officer (IAS/IPS)', NULL, 'Civil Services Officer (IAS/IPS)', 'Competitive Salary', '2025-12-31 05:00:22'),
(40, 9, 'Science (PCMB) to Indian Army – Tradesman / GD', NULL, 'Indian Army – Tradesman / GD', 'Competitive Salary', '2026-01-02 04:17:24'),
(41, 9, 'Science (PCM) to Software Engineer', NULL, 'Software Engineer', 'Competitive Salary', '2026-01-02 08:52:39'),
(42, 9, 'Post-Doctoral Fellowship (Post-Doc) to Research Scientist', NULL, 'Research Scientist', 'Competitive Salary', '2026-01-02 09:09:02'),
(43, 9, 'Science (PCMB) to Railway Group D', NULL, 'Railway Group D', 'Competitive Salary', '2026-01-02 10:06:36'),
(44, 9, 'Science (PCMB) to Indian Army – Tradesman / GD', NULL, 'Indian Army – Tradesman / GD', 'Competitive Salary', '2026-01-02 10:07:19'),
(45, 9, 'Architecture (B.Arch) to Software Engineer', NULL, 'Software Engineer', 'Competitive Salary', '2026-01-02 10:08:06'),
(46, 9, 'Vocational / ITI to Bank IT Officer (IBPS / SBI – Specialist Cadre)', NULL, 'Bank IT Officer (IBPS / SBI – Specialist Cadre)', 'Competitive Salary', '2026-01-02 10:40:37'),
(47, 9, 'BHM (Bachelor of Hotel Management) to Catering Officer (Railways / Defence)', NULL, 'Catering Officer (Railways / Defence)', 'Competitive Salary', '2026-01-02 10:43:27'),
(48, 9, 'Commerce to Indian Army – Tradesman / GD', NULL, 'Indian Army – Tradesman / GD', 'Competitive Salary', '2026-01-02 10:56:12'),
(49, 9, 'BBA (Arts Stream) to Business Analyst', NULL, 'Business Analyst', 'Competitive Salary', '2026-01-02 10:58:08'),
(50, 9, 'BBA (Arts Stream) to Civil Engineer', NULL, 'Civil Engineer', 'Competitive Salary', '2026-01-02 10:59:33'),
(51, 9, 'Science (PCMB) to Business Analyst', NULL, 'Business Analyst', 'Competitive Salary', '2026-01-02 11:03:33'),
(52, 9, 'Law (LLB) to Legal Advisor in Govt / PSUs', NULL, 'Legal Advisor in Govt / PSUs', 'Competitive Salary', '2026-01-02 11:23:37'),
(53, 9, 'BCA (Bachelor of Computer Applications) to Data Analyst', NULL, 'Data Analyst', 'Competitive Salary', '2026-01-02 11:37:24'),
(54, 9, 'Law (LLB) to Legal Advisor in Govt / PSUs', NULL, 'Legal Advisor in Govt / PSUs', 'Competitive Salary', '2026-01-02 11:45:41'),
(58, 13, 'MA (Master of Arts) to Content Writer / Editor', NULL, 'Content Writer / Editor', 'Competitive Salary', '2026-01-02 13:21:54'),
(60, 11, 'Arts / Humanities to Police Constable', NULL, 'Police Constable', 'Competitive Salary', '2026-01-02 13:45:35'),
(61, 11, 'M.Tech to Research Scientist', NULL, 'Research Scientist', 'Competitive Salary', '2026-01-03 04:37:03'),
(62, 11, 'Commerce to Finance Manager', NULL, 'Finance Manager', 'Competitive Salary', '2026-01-03 10:17:38'),
(63, 11, 'Science (PCM) to Software Engineer', NULL, 'Software Engineer', 'Competitive Salary', '2026-01-04 05:09:51');

-- --------------------------------------------------------

--
-- Table structure for table `user_roadmap_steps`
--

CREATE TABLE `user_roadmap_steps` (
  `step_id` int(11) NOT NULL,
  `roadmap_id` int(11) NOT NULL,
  `step_order` int(11) NOT NULL,
  `step_type` enum('EDUCATION_LEVEL','STREAM','EXAM','JOB') NOT NULL,
  `reference_id` int(11) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `icon` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_roadmap_steps`
--

INSERT INTO `user_roadmap_steps` (`step_id`, `roadmap_id`, `step_order`, `step_type`, `reference_id`, `title`, `description`, `icon`) VALUES
(1, 1, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(2, 1, 2, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(3, 1, 3, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(4, 1, 4, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(5, 1, 5, 'EXAM', 4, 'JEE Main', 'Entrance exam for higher education', 'ic_exam'),
(6, 1, 6, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(7, 1, 7, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(8, 1, 8, 'JOB', 1, 'Software Engineer', 'Target Role', 'ic_briefcase'),
(9, 2, 1, 'EDUCATION_LEVEL', 7, 'Postgraduate', 'Selected education level', 'ic_education'),
(10, 2, 2, 'EXAM', 17, 'NET', 'Entrance exam for higher education', 'ic_exam'),
(11, 2, 3, 'STREAM', 24, 'PhD', 'Doctoral research program', 'ic_stream'),
(12, 2, 4, 'JOB', 14, 'Research Scientist', 'Target Role', 'ic_briefcase'),
(13, 3, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(14, 3, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(15, 3, 3, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(16, 3, 4, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(17, 3, 5, 'STREAM', 22, 'M.Tech', 'Postgraduate engineering degree', 'ic_stream'),
(18, 3, 6, 'STREAM', 21, 'MBA', 'Postgraduate management degree', 'ic_stream'),
(19, 3, 7, 'JOB', 8, 'Bank Probationary Officer', 'Target Role', 'ic_briefcase'),
(20, 4, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(21, 4, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(22, 4, 3, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(23, 4, 4, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(24, 4, 5, 'STREAM', 22, 'M.Tech', 'Postgraduate engineering degree', 'ic_stream'),
(25, 4, 6, 'STREAM', 21, 'MBA', 'Postgraduate management degree', 'ic_stream'),
(26, 4, 7, 'JOB', 8, 'Bank Probationary Officer', 'Target Role', 'ic_briefcase'),
(27, 4, 8, 'STREAM', 10, 'Architecture (B.Arch)', 'Architecture & design degree', 'ic_stream'),
(28, 4, 9, 'STREAM', 9, 'Pure Sciences (B.Sc)', 'Academic science degree', 'ic_stream'),
(29, 4, 10, 'STREAM', 24, 'PhD', 'Doctoral research program', 'ic_stream'),
(30, 4, 11, 'JOB', 14, 'Research Scientist', 'Target Role', 'ic_briefcase'),
(31, 5, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(32, 5, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(33, 5, 3, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(34, 5, 4, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(35, 5, 5, 'STREAM', 22, 'M.Tech', 'Postgraduate engineering degree', 'ic_stream'),
(36, 5, 6, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(37, 6, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(38, 6, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(39, 6, 3, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(40, 6, 4, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(41, 6, 5, 'EXAM', 4, 'JEE Main', 'Entrance exam for higher education', 'ic_exam'),
(42, 6, 6, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(43, 6, 7, 'JOB', 1, 'Software Engineer', 'Target Role', 'ic_briefcase'),
(44, 7, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(45, 7, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(46, 7, 3, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(47, 7, 4, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(48, 7, 5, 'EXAM', 4, 'JEE Main', 'Entrance exam for higher education', 'ic_exam'),
(49, 7, 6, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(50, 7, 7, 'JOB', 1, 'Software Engineer', 'Target Role', 'ic_briefcase'),
(51, 7, 8, 'JOB', 1, 'Software Engineer', 'Target Role', 'ic_briefcase'),
(52, 8, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(53, 8, 2, 'STREAM', 2, 'Science (PCB)', 'Science with Physics, Chemistry, Biology', 'ic_stream'),
(54, 8, 3, 'STREAM', 8, 'Medical (MBBS / BDS)', 'Professional medical education', 'ic_stream'),
(55, 8, 4, 'STREAM', 24, 'PhD', 'Doctoral research program', 'ic_stream'),
(56, 8, 5, 'JOB', 14, 'Research Scientist', 'Target Role', 'ic_briefcase'),
(57, 9, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(58, 9, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(59, 9, 3, 'STREAM', 2, 'Science (PCB)', 'Science with Physics, Chemistry, Biology', 'ic_stream'),
(60, 9, 4, 'EXAM', 6, 'NEET', 'Entrance exam for higher education', 'ic_exam'),
(61, 9, 5, 'STREAM', 8, 'Medical (MBBS / BDS)', 'Professional medical education', 'ic_stream'),
(62, 9, 6, 'JOB', 5, 'Doctor (MBBS)', 'Target Role', 'ic_briefcase'),
(63, 10, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(64, 10, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(65, 10, 3, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(66, 10, 4, 'EXAM', 5, 'JEE Advanced', 'Entrance exam for higher education', 'ic_exam'),
(67, 10, 5, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(68, 10, 6, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(69, 11, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(70, 11, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(71, 11, 3, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(72, 11, 4, 'EXAM', 7, 'BITSAT', 'Entrance exam for higher education', 'ic_exam'),
(73, 11, 5, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(74, 11, 6, 'JOB', 1, 'Software Engineer', 'Target Role', 'ic_briefcase'),
(75, 12, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(76, 12, 2, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(77, 12, 3, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(78, 12, 4, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(79, 13, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(80, 13, 2, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(81, 13, 3, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(82, 13, 4, 'STREAM', 9, 'Pure Sciences (B.Sc)', 'Academic science degree', 'ic_stream'),
(83, 13, 5, 'STREAM', 24, 'PhD', 'Doctoral research program', 'ic_stream'),
(84, 13, 6, 'JOB', 14, 'Research Scientist', 'Target Role', 'ic_briefcase'),
(85, 14, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(86, 14, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(87, 14, 3, 'STREAM', 2, 'Science (PCB)', 'Science with Physics, Chemistry, Biology', 'ic_stream'),
(88, 14, 4, 'EXAM', 6, 'NEET', 'Entrance exam for higher education', 'ic_exam'),
(89, 14, 5, 'STREAM', 8, 'Medical (MBBS / BDS)', 'Professional medical education', 'ic_stream'),
(90, 14, 6, 'JOB', 5, 'Doctor (MBBS)', 'Target Role', 'ic_briefcase'),
(91, 15, 1, 'EDUCATION_LEVEL', 2, '12th Science', 'Selected education level', 'ic_education'),
(92, 15, 2, 'EXAM', 4, 'JEE Main', 'Entrance exam for higher education', 'ic_exam'),
(93, 15, 3, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(94, 15, 4, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(95, 16, 1, 'EDUCATION_LEVEL', 2, '12th Science', 'Selected education level', 'ic_education'),
(96, 16, 2, 'STREAM', 11, 'Pharmacy (B.Pharm)', 'Pharmaceutical sciences', 'ic_stream'),
(97, 16, 3, 'JOB', 6, 'Pharmacist', 'Target Role', 'ic_briefcase'),
(98, 17, 1, 'EDUCATION_LEVEL', 6, 'Undergraduate', 'Selected education level', 'ic_education'),
(99, 17, 2, 'EXAM', 16, 'CAT', 'Entrance exam for higher education', 'ic_exam'),
(100, 17, 3, 'STREAM', 21, 'MBA', 'Postgraduate management degree', 'ic_stream'),
(101, 17, 4, 'JOB', 8, 'Bank Probationary Officer', 'Target Role', 'ic_briefcase'),
(102, 18, 1, 'EDUCATION_LEVEL', 7, 'Postgraduate', 'Selected education level', 'ic_education'),
(103, 18, 2, 'STREAM', 24, 'PhD', 'Doctoral research program', 'ic_stream'),
(104, 18, 3, 'JOB', 13, 'Professor', 'Target Role', 'ic_briefcase'),
(105, 19, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(106, 19, 2, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(107, 19, 3, 'EXAM', 4, 'JEE Main', 'Entrance exam for higher education', 'ic_exam'),
(108, 19, 4, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(109, 19, 5, 'JOB', 3, 'Civil Engineer', 'Target Role', 'ic_briefcase'),
(110, 20, 1, 'EDUCATION_LEVEL', 2, '', '', 'ic_stream'),
(111, 20, 2, 'STREAM', 7, '', '', 'ic_stream'),
(112, 20, 3, 'JOB', 3, '', '', 'ic_stream'),
(113, 21, 1, 'EDUCATION_LEVEL', 2, '', '', 'ic_stream'),
(114, 21, 2, 'STREAM', 7, '', '', 'ic_stream'),
(115, 21, 3, 'JOB', 3, '', '', 'ic_stream'),
(116, 22, 1, 'EDUCATION_LEVEL', 2, '', '', 'ic_stream'),
(117, 22, 2, 'STREAM', 7, '', '', 'ic_stream'),
(118, 22, 3, 'JOB', 3, '', '', 'ic_stream'),
(119, 23, 1, 'EDUCATION_LEVEL', 2, '', '', 'ic_stream'),
(120, 23, 2, 'STREAM', 7, '', '', 'ic_stream'),
(121, 23, 3, 'JOB', 3, '', '', 'ic_stream'),
(122, 24, 1, '', 2, '12th Science', 'Current education level', 'ic_education'),
(123, 24, 2, 'STREAM', 8, 'Medical (MBBS / BDS)', 'Selected academic stream', 'ic_stream'),
(124, 24, 3, '', 0, 'Higher Education', 'Continue your academic journey', 'ic_graduation'),
(125, 24, 4, '', 0, 'Internships & Projects', 'Gain practical experience and build a real-world portfolio.', 'ic_briefcase'),
(126, 24, 5, '', 0, 'Interviews & Apps', 'Resume building, mock interviews, and technical screening prep.', 'ic_document'),
(127, 24, 6, 'JOB', 5, 'Doctor (MBBS)', 'Achieve your dream role at a top company.', 'ic_briefcase'),
(128, 25, 1, '', 2, '12th Science', 'Current education level', 'ic_education'),
(129, 25, 2, 'STREAM', 8, 'Medical (MBBS / BDS)', 'Selected academic stream', 'ic_stream'),
(130, 25, 3, '', 0, 'Higher Education', 'Continue your academic journey', 'ic_graduation'),
(131, 25, 4, '', 0, 'Internships & Projects', 'Gain practical experience and build a real-world portfolio.', 'ic_briefcase'),
(132, 25, 5, '', 0, 'Interviews & Apps', 'Resume building, mock interviews, and technical screening prep.', 'ic_document'),
(133, 25, 6, 'JOB', 5, 'Doctor (MBBS)', 'Achieve your dream role at a top company.', 'ic_briefcase'),
(134, 26, 1, '', 2, '12th Science', 'Current education level', 'ic_education'),
(135, 26, 2, 'STREAM', 8, 'Medical (MBBS / BDS)', 'Selected academic stream', 'ic_stream'),
(136, 26, 3, '', 0, 'Higher Education', 'Continue your academic journey', 'ic_graduation'),
(137, 26, 4, '', 0, 'Internships & Projects', 'Gain practical experience and build a real-world portfolio.', 'ic_briefcase'),
(138, 26, 5, '', 0, 'Interviews & Apps', 'Resume building, mock interviews, and technical screening prep.', 'ic_document'),
(139, 26, 6, 'JOB', 5, 'Doctor (MBBS)', 'Achieve your dream role at a top company.', 'ic_briefcase'),
(140, 27, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(141, 27, 2, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(142, 27, 3, 'EXAM', 2, 'ITI Entrance Test', 'Entrance exam for higher education', 'ic_exam'),
(143, 27, 4, 'STREAM', 6, 'Vocational / ITI', 'Skill-based vocational education', 'ic_stream'),
(144, 27, 5, 'EXAM', 1, 'Polytechnic Common Entrance Test (POLYCET)', 'Entrance exam for higher education', 'ic_exam'),
(145, 27, 6, 'STREAM', 18, 'Diploma', 'Technical diploma after 10th', 'ic_stream'),
(146, 27, 7, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(147, 28, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(148, 28, 2, 'EXAM', 2, 'ITI Entrance Test', 'Entrance exam for higher education', 'ic_exam'),
(149, 28, 3, 'STREAM', 6, 'Vocational / ITI', 'Skill-based vocational education', 'ic_stream'),
(150, 28, 4, 'EXAM', 1, 'Polytechnic Common Entrance Test (POLYCET)', 'Entrance exam for higher education', 'ic_exam'),
(151, 28, 5, 'STREAM', 18, 'Diploma', 'Technical diploma after 10th', 'ic_stream'),
(152, 28, 6, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(153, 29, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(154, 29, 2, 'EXAM', 2, 'ITI Entrance Test', 'Entrance exam for higher education', 'ic_exam'),
(155, 29, 3, 'STREAM', 6, 'Vocational / ITI', 'Skill-based vocational education', 'ic_stream'),
(156, 29, 4, 'EXAM', 1, 'Polytechnic Common Entrance Test (POLYCET)', 'Entrance exam for higher education', 'ic_exam'),
(157, 29, 5, 'STREAM', 18, 'Diploma', 'Technical diploma after 10th', 'ic_stream'),
(158, 29, 6, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(159, 29, 7, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(160, 29, 8, 'EXAM', 5, 'JEE Advanced', 'Entrance exam for higher education', 'ic_exam'),
(161, 29, 9, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(162, 29, 10, 'JOB', 9, 'Business Analyst', 'Target Role', 'ic_briefcase'),
(163, 30, 1, 'EDUCATION_LEVEL', 5, 'Diploma', 'Selected education level', 'ic_education'),
(164, 30, 2, 'EXAM', 14, 'Lateral Entry Entrance Test (LEET)', 'Entrance exam for higher education', 'ic_exam'),
(165, 30, 3, 'STREAM', 20, 'B.Tech (Lateral Entry)', 'Engineering via lateral entry', 'ic_stream'),
(166, 30, 4, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(167, 31, 1, 'EDUCATION_LEVEL', 5, 'Diploma', 'Selected education level', 'ic_education'),
(168, 31, 2, 'STREAM', 19, 'Advanced Diploma', 'Specialized diploma programs', 'ic_stream'),
(169, 31, 3, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(170, 32, 1, 'EDUCATION_LEVEL', 2, '12th Science', 'Selected education level', 'ic_education'),
(171, 32, 2, 'EXAM', 6, 'NEET', 'Entrance exam for higher education', 'ic_exam'),
(172, 32, 3, 'STREAM', 8, 'Medical (MBBS / BDS)', 'Professional medical education', 'ic_stream'),
(173, 32, 4, 'JOB', 5, 'Doctor (MBBS)', 'Target Role', 'ic_briefcase'),
(174, 33, 1, 'EDUCATION_LEVEL', 3, '12th Commerce', 'Selected education level', 'ic_education'),
(175, 33, 2, 'EXAM', 9, 'CUET (UG)', 'Entrance exam for higher education', 'ic_exam'),
(176, 33, 3, 'STREAM', 13, 'BBA', 'Business administration degree', 'ic_stream'),
(177, 33, 4, 'STREAM', 21, 'MBA', 'Postgraduate management degree', 'ic_stream'),
(178, 33, 5, 'JOB', 9, 'Business Analyst', 'Target Role', 'ic_briefcase'),
(179, 34, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(180, 34, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(181, 34, 3, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(182, 34, 4, 'EXAM', 4, 'JEE Main', 'Entrance exam for higher education', 'ic_exam'),
(183, 34, 5, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(184, 34, 6, 'JOB', 4, 'Mechanical Engineer', 'Target Role', 'ic_briefcase'),
(185, 35, 1, 'EDUCATION_LEVEL', 7, 'Postgraduate', 'Selected education level', 'ic_education'),
(186, 35, 2, 'STREAM', 25, 'Professional Certification', 'Industry certifications', 'ic_stream'),
(187, 35, 3, 'JOB', 2, 'Data Analyst', 'Target Role', 'ic_briefcase'),
(188, 36, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(189, 36, 2, 'EXAM', 3, 'National Means-cum-Merit Scholarship (NMMS)', 'Entrance exam for higher education', 'ic_exam'),
(190, 36, 3, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(191, 36, 4, 'EXAM', 4, 'JEE Main', 'Entrance exam for higher education', 'ic_exam'),
(192, 36, 5, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(193, 36, 6, 'JOB', 1, 'Software Engineer', 'Target Role', 'ic_briefcase'),
(194, 37, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(195, 37, 2, 'EXAM', 3, 'Sainik School Entrance Exam (AISSEE)', 'Entrance exam for higher education', 'ic_exam'),
(196, 37, 3, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(197, 37, 4, 'STREAM', 10, 'Architecture (B.Arch)', 'Architecture & design degree', 'ic_stream'),
(198, 37, 5, 'JOB', 3, 'Civil Engineer', 'Target Role', 'ic_briefcase'),
(199, 38, 1, 'EDUCATION_LEVEL', 4, '12th Arts', 'Selected education level', 'ic_education'),
(200, 38, 2, 'EXAM', 13, 'CUET- UG (Arts)', 'Entrance exam for higher education', 'ic_exam'),
(201, 38, 3, 'STREAM', 15, 'BA', 'Arts undergraduate degree', 'ic_stream'),
(202, 38, 4, 'JOB', 12, 'Journalist', 'Target Role', 'ic_briefcase'),
(203, 39, 1, 'EDUCATION_LEVEL', 4, '12th Arts', 'Selected education level', 'ic_education'),
(204, 39, 2, 'STREAM', 16, 'Law (LLB)', 'Professional law degree', 'ic_stream'),
(205, 39, 3, 'JOB', 10, 'Civil Services Officer (IAS/IPS)', 'Target Role', 'ic_briefcase'),
(206, 40, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(207, 40, 2, 'EXAM', 3, 'Sainik School Entrance Exam (AISSEE)', 'Entrance exam for higher education', 'ic_exam'),
(208, 40, 3, 'STREAM', 3, 'Science (PCMB)', 'Science with PCM + Biology', 'ic_stream'),
(209, 40, 4, 'JOB', 55, 'Indian Army – Tradesman / GD', 'Target Role', 'ic_briefcase'),
(210, 41, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(211, 41, 2, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(212, 41, 3, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(213, 41, 4, 'JOB', 1, 'Software Engineer', 'Target Role', 'ic_briefcase'),
(214, 42, 1, 'EDUCATION_LEVEL', 7, 'Postgraduate', 'Selected education level', 'ic_education'),
(215, 42, 2, 'STREAM', 37, 'Post-Doctoral Fellowship (Post-Doc)', 'Advanced research program undertaken after completion of a PhD.', 'ic_stream'),
(216, 42, 3, 'JOB', 14, 'Research Scientist', 'Target Role', 'ic_briefcase'),
(217, 43, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(218, 43, 2, 'EXAM', 3, 'Sainik School Entrance Exam (AISSEE)', 'Entrance exam for higher education', 'ic_exam'),
(219, 43, 3, 'STREAM', 3, 'Science (PCMB)', 'Science with PCM + Biology', 'ic_stream'),
(220, 43, 4, 'JOB', 54, 'Railway Group D', 'Target Role', 'ic_briefcase'),
(221, 44, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(222, 44, 2, 'STREAM', 3, 'Science (PCMB)', 'Science with PCM + Biology', 'ic_stream'),
(223, 44, 3, 'JOB', 55, 'Indian Army – Tradesman / GD', 'Target Role', 'ic_briefcase'),
(224, 45, 1, 'EDUCATION_LEVEL', 2, '12th Science', 'Selected education level', 'ic_education'),
(225, 45, 2, 'STREAM', 10, 'Architecture (B.Arch)', 'Architecture & design degree', 'ic_stream'),
(226, 45, 3, 'STREAM', 22, 'M.Tech', 'Postgraduate engineering degree', 'ic_stream'),
(227, 45, 4, 'JOB', 1, 'Software Engineer', 'Target Role', 'ic_briefcase'),
(228, 46, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(229, 46, 2, 'EXAM', 1, 'Polytechnic Common Entrance Test (POLYCET)', 'Entrance exam for higher education', 'ic_exam'),
(230, 46, 3, 'STREAM', 6, 'Vocational / ITI', 'Skill-based vocational education', 'ic_stream'),
(231, 46, 4, 'STREAM', 19, 'Advanced Diploma', 'Specialized diploma programs', 'ic_stream'),
(232, 46, 5, 'STREAM', 33, 'B.Sc (Computer Science / IT)', 'Undergraduate program focused on computer science theory and information technology fundamentals.', 'ic_stream'),
(233, 46, 6, 'JOB', 44, 'Bank IT Officer (IBPS / SBI – Specialist Cadre)', 'Target Role', 'ic_briefcase'),
(234, 47, 1, 'EDUCATION_LEVEL', 4, '12th Arts', 'Selected education level', 'ic_education'),
(235, 47, 2, 'STREAM', 31, 'BHM (Bachelor of Hotel Management)', 'Undergraduate program focused on hospitality and hotel administration.', 'ic_stream'),
(236, 47, 3, 'JOB', 39, 'Catering Officer (Railways / Defence)', 'Target Role', 'ic_briefcase'),
(237, 48, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(238, 48, 2, 'STREAM', 4, 'Commerce', 'Business & finance oriented stream', 'ic_stream'),
(239, 48, 3, 'STREAM', 12, 'B.Com', 'Commerce undergraduate degree', 'ic_stream'),
(240, 48, 4, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(241, 48, 5, 'EXAM', 3, 'Sainik School Entrance Exam (AISSEE)', 'Entrance exam for higher education', 'ic_exam'),
(242, 48, 6, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(243, 48, 7, 'JOB', 55, 'Indian Army – Tradesman / GD', 'Target Role', 'ic_briefcase'),
(244, 49, 1, 'EDUCATION_LEVEL', 4, '12th Arts', 'Selected education level', 'ic_education'),
(245, 49, 2, 'EXAM', 31, 'CUET-UG (Commerce)', 'Entrance exam for higher education', 'ic_exam'),
(246, 49, 3, 'STREAM', 29, 'BBA (Arts Stream)', 'Undergraduate program focused on business administration and management principles.', 'ic_stream'),
(247, 49, 4, 'STREAM', 21, 'MBA', 'Postgraduate management degree', 'ic_stream'),
(248, 49, 5, 'JOB', 9, 'Business Analyst', 'Target Role', 'ic_briefcase'),
(249, 50, 1, 'EDUCATION_LEVEL', 4, '12th Arts', 'Selected education level', 'ic_education'),
(250, 50, 2, 'EXAM', 31, 'CUET-UG (Commerce)', 'Entrance exam for higher education', 'ic_exam'),
(251, 50, 3, 'STREAM', 29, 'BBA (Arts Stream)', 'Undergraduate program focused on business administration and management principles.', 'ic_stream'),
(252, 50, 4, 'STREAM', 21, 'MBA', 'Postgraduate management degree', 'ic_stream'),
(253, 50, 5, 'JOB', 9, 'Business Analyst', 'Target Role', 'ic_briefcase'),
(254, 50, 6, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(255, 50, 7, 'EXAM', 3, 'Sainik School Entrance Exam (AISSEE)', 'Entrance exam for higher education', 'ic_exam'),
(256, 50, 8, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(257, 50, 9, 'EXAM', 5, 'JEE Advanced', 'Entrance exam for higher education', 'ic_exam'),
(258, 50, 10, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(259, 50, 11, 'JOB', 3, 'Civil Engineer', 'Target Role', 'ic_briefcase'),
(260, 51, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(261, 51, 2, 'STREAM', 3, 'Science (PCMB)', 'Science with PCM + Biology', 'ic_stream'),
(262, 51, 3, 'EXAM', 3, 'Sainik School Entrance Exam (AISSEE)', 'Entrance exam for higher education', 'ic_exam'),
(263, 51, 4, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(264, 51, 5, 'EXAM', 7, 'BITSAT', 'Entrance exam for higher education', 'ic_exam'),
(265, 51, 6, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(266, 51, 7, 'EDUCATION_LEVEL', 3, '12th Commerce', 'Selected education level', 'ic_education'),
(267, 51, 8, 'EXAM', 31, 'CUET-UG (Commerce)', 'Entrance exam for higher education', 'ic_exam'),
(268, 51, 9, 'STREAM', 12, 'B.Com', 'Commerce undergraduate degree', 'ic_stream'),
(269, 51, 10, 'EXAM', 16, 'CAT', 'Entrance exam for higher education', 'ic_exam'),
(270, 51, 11, 'STREAM', 21, 'MBA', 'Postgraduate management degree', 'ic_stream'),
(271, 51, 12, 'JOB', 9, 'Business Analyst', 'Target Role', 'ic_briefcase'),
(272, 52, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(273, 52, 2, 'EDUCATION_LEVEL', 4, '12th Arts', 'Selected education level', 'ic_education'),
(274, 52, 3, 'EXAM', 12, 'CLAT UG', 'Entrance exam for higher education', 'ic_exam'),
(275, 52, 4, 'STREAM', 16, 'Law (LLB)', 'Professional law degree', 'ic_stream'),
(276, 52, 5, 'STREAM', 36, 'LLM (Master of Laws)', 'Postgraduate law program offering advanced legal education and specialization.', 'ic_stream'),
(277, 52, 6, 'EXAM', 25, 'Common Law Admission Test – PG (CLAT-PG)', 'Entrance exam for higher education', 'ic_exam'),
(278, 52, 7, 'STREAM', 36, 'LLM (Master of Laws)', 'Postgraduate law program offering advanced legal education and specialization.', 'ic_stream'),
(279, 52, 8, 'JOB', 51, 'Legal Advisor in Govt / PSUs', 'Target Role', 'ic_briefcase'),
(280, 53, 1, 'EDUCATION_LEVEL', 5, 'Diploma', 'Selected education level', 'ic_education'),
(281, 53, 2, 'STREAM', 32, 'BCA (Bachelor of Computer Applications)', 'Undergraduate program focused on computer applications, software development, and programming.', 'ic_stream'),
(282, 53, 3, 'JOB', 2, 'Data Analyst', 'Target Role', 'ic_briefcase'),
(283, 54, 1, 'EDUCATION_LEVEL', 4, '12th Arts', 'Selected education level', 'ic_education'),
(284, 54, 2, 'STREAM', 16, 'Law (LLB)', 'Professional law degree', 'ic_stream'),
(285, 54, 3, 'STREAM', 36, 'LLM (Master of Laws)', 'Postgraduate law program offering advanced legal education and specialization.', 'ic_stream'),
(286, 54, 4, 'JOB', 51, 'Legal Advisor in Govt / PSUs', 'Target Role', 'ic_briefcase'),
(299, 58, 1, 'EDUCATION_LEVEL', 6, 'Undergraduate', 'Selected education level', 'ic_education'),
(300, 58, 2, 'STREAM', 34, 'MA (Master of Arts)', 'Postgraduate program focused on advanced studies in arts, humanities, and social sciences.', 'ic_stream'),
(301, 58, 3, 'JOB', 47, 'Content Writer / Editor', 'Target Role', 'ic_briefcase'),
(306, 60, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(307, 60, 2, 'STREAM', 5, 'Arts / Humanities', 'Humanities & social sciences', 'ic_stream'),
(308, 60, 3, 'JOB', 53, 'Police Constable', 'Target Role', 'ic_briefcase'),
(309, 61, 1, 'EDUCATION_LEVEL', 6, 'Undergraduate', 'Selected education level', 'ic_education'),
(310, 61, 2, 'EXAM', 15, 'GATE', 'Entrance exam for higher education', 'ic_exam'),
(311, 61, 3, 'STREAM', 22, 'M.Tech', 'Postgraduate engineering degree', 'ic_stream'),
(312, 61, 4, 'EXAM', 28, 'State Eligibility Test (SET)', 'Entrance exam for higher education', 'ic_exam'),
(313, 61, 5, 'STREAM', 24, 'PhD', 'Doctoral research program', 'ic_stream'),
(314, 61, 6, 'JOB', 14, 'Research Scientist', 'Target Role', 'ic_briefcase'),
(315, 62, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(316, 62, 2, 'STREAM', 4, 'Commerce', 'Business & finance oriented stream', 'ic_stream'),
(317, 62, 3, 'EXAM', 31, 'CUET-UG (Commerce)', 'Entrance exam for higher education', 'ic_exam'),
(318, 62, 4, 'STREAM', 12, 'B.Com', 'Commerce undergraduate degree', 'ic_stream'),
(319, 62, 5, 'STREAM', 21, 'MBA', 'Postgraduate management degree', 'ic_stream'),
(320, 62, 6, 'JOB', 23, 'Finance Manager', 'Target Role', 'ic_briefcase'),
(321, 63, 1, 'EDUCATION_LEVEL', 1, '10th Pass', 'Selected education level', 'ic_education'),
(322, 63, 2, 'EXAM', 3, 'Sainik School Entrance Exam (AISSEE)', 'Entrance exam for higher education', 'ic_exam'),
(323, 63, 3, 'STREAM', 1, 'Science (PCM)', 'Science with Physics, Chemistry, Mathematics', 'ic_stream'),
(324, 63, 4, 'EXAM', 7, 'BITSAT', 'Entrance exam for higher education', 'ic_exam'),
(325, 63, 5, 'STREAM', 7, 'Engineering (B.Tech)', 'Undergraduate engineering degree', 'ic_stream'),
(326, 63, 6, 'JOB', 1, 'Software Engineer', 'Target Role', 'ic_briefcase');

-- --------------------------------------------------------

--
-- Table structure for table `user_settings`
--

CREATE TABLE `user_settings` (
  `user_id` int(11) NOT NULL,
  `personalization_enabled` tinyint(1) DEFAULT 1,
  `exam_alerts` tinyint(1) DEFAULT 1,
  `data_visibility` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ai_chat_history`
--
ALTER TABLE `ai_chat_history`
  ADD PRIMARY KEY (`chat_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `education_levels`
--
ALTER TABLE `education_levels`
  ADD PRIMARY KEY (`education_level_id`);

--
-- Indexes for table `education_level_exams`
--
ALTER TABLE `education_level_exams`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_level_exam` (`education_level_id`,`exam_id`),
  ADD KEY `exam_id` (`exam_id`);

--
-- Indexes for table `education_level_jobs`
--
ALTER TABLE `education_level_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_level_job` (`education_level_id`,`job_id`),
  ADD KEY `job_id` (`job_id`);

--
-- Indexes for table `entrance_exams`
--
ALTER TABLE `entrance_exams`
  ADD PRIMARY KEY (`exam_id`);

--
-- Indexes for table `forum_answers`
--
ALTER TABLE `forum_answers`
  ADD PRIMARY KEY (`answer_id`),
  ADD KEY `question_id` (`question_id`);

--
-- Indexes for table `forum_answer_likes`
--
ALTER TABLE `forum_answer_likes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `answer_id` (`answer_id`,`user_id`);

--
-- Indexes for table `forum_likes`
--
ALTER TABLE `forum_likes`
  ADD PRIMARY KEY (`like_id`),
  ADD UNIQUE KEY `unique_question_like` (`user_id`,`question_id`),
  ADD UNIQUE KEY `unique_answer_like` (`user_id`,`answer_id`),
  ADD KEY `question_id` (`question_id`),
  ADD KEY `answer_id` (`answer_id`);

--
-- Indexes for table `forum_questions`
--
ALTER TABLE `forum_questions`
  ADD PRIMARY KEY (`question_id`),
  ADD KEY `education_level_id` (`education_level_id`);

--
-- Indexes for table `forum_replies`
--
ALTER TABLE `forum_replies`
  ADD PRIMARY KEY (`reply_id`),
  ADD KEY `answer_id` (`answer_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `jobs`
--
ALTER TABLE `jobs`
  ADD PRIMARY KEY (`job_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `idx_user_read` (`user_id`,`is_read`),
  ADD KEY `idx_created` (`created_at`),
  ADD KEY `from_user_id` (`from_user_id`);

--
-- Indexes for table `roadmaps`
--
ALTER TABLE `roadmaps`
  ADD PRIMARY KEY (`roadmap_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `roadmap_steps`
--
ALTER TABLE `roadmap_steps`
  ADD PRIMARY KEY (`step_id`),
  ADD KEY `roadmap_id` (`roadmap_id`);

--
-- Indexes for table `saved_exams`
--
ALTER TABLE `saved_exams`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_user_exam` (`user_id`,`exam_id`),
  ADD KEY `exam_id` (`exam_id`);

--
-- Indexes for table `saved_jobs`
--
ALTER TABLE `saved_jobs`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `saved_roadmaps`
--
ALTER TABLE `saved_roadmaps`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_user_roadmap` (`user_id`,`roadmap_id`),
  ADD KEY `roadmap_id` (`roadmap_id`);

--
-- Indexes for table `streams`
--
ALTER TABLE `streams`
  ADD PRIMARY KEY (`stream_id`);

--
-- Indexes for table `streams_old`
--
ALTER TABLE `streams_old`
  ADD PRIMARY KEY (`stream_id`),
  ADD KEY `education_level_id` (`education_level_id`);

--
-- Indexes for table `stream_exams`
--
ALTER TABLE `stream_exams`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_stream_exam` (`stream_id`,`exam_id`),
  ADD KEY `fk_stream_exams_exam` (`exam_id`);

--
-- Indexes for table `stream_jobs`
--
ALTER TABLE `stream_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_stream_job` (`stream_id`,`job_id`),
  ADD KEY `stream_jobs_ibfk_2` (`job_id`);

--
-- Indexes for table `stream_progression`
--
ALTER TABLE `stream_progression`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_progression` (`current_stream_id`,`next_stream_id`),
  ADD UNIQUE KEY `uniq_stream_path` (`current_stream_id`,`next_stream_id`),
  ADD KEY `fk_stream_progression_next` (`next_stream_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_roadmaps`
--
ALTER TABLE `user_roadmaps`
  ADD PRIMARY KEY (`roadmap_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `user_roadmap_steps`
--
ALTER TABLE `user_roadmap_steps`
  ADD PRIMARY KEY (`step_id`),
  ADD KEY `roadmap_id` (`roadmap_id`);

--
-- Indexes for table `user_settings`
--
ALTER TABLE `user_settings`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ai_chat_history`
--
ALTER TABLE `ai_chat_history`
  MODIFY `chat_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `education_levels`
--
ALTER TABLE `education_levels`
  MODIFY `education_level_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `education_level_exams`
--
ALTER TABLE `education_level_exams`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=64;

--
-- AUTO_INCREMENT for table `education_level_jobs`
--
ALTER TABLE `education_level_jobs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=126;

--
-- AUTO_INCREMENT for table `entrance_exams`
--
ALTER TABLE `entrance_exams`
  MODIFY `exam_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT for table `forum_answers`
--
ALTER TABLE `forum_answers`
  MODIFY `answer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `forum_answer_likes`
--
ALTER TABLE `forum_answer_likes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `forum_likes`
--
ALTER TABLE `forum_likes`
  MODIFY `like_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `forum_questions`
--
ALTER TABLE `forum_questions`
  MODIFY `question_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `forum_replies`
--
ALTER TABLE `forum_replies`
  MODIFY `reply_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `jobs`
--
ALTER TABLE `jobs`
  MODIFY `job_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=99;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `roadmaps`
--
ALTER TABLE `roadmaps`
  MODIFY `roadmap_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=112;

--
-- AUTO_INCREMENT for table `roadmap_steps`
--
ALTER TABLE `roadmap_steps`
  MODIFY `step_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=462;

--
-- AUTO_INCREMENT for table `saved_exams`
--
ALTER TABLE `saved_exams`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `saved_jobs`
--
ALTER TABLE `saved_jobs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `saved_roadmaps`
--
ALTER TABLE `saved_roadmaps`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `streams`
--
ALTER TABLE `streams`
  MODIFY `stream_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT for table `streams_old`
--
ALTER TABLE `streams_old`
  MODIFY `stream_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `stream_exams`
--
ALTER TABLE `stream_exams`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=99;

--
-- AUTO_INCREMENT for table `stream_jobs`
--
ALTER TABLE `stream_jobs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=324;

--
-- AUTO_INCREMENT for table `stream_progression`
--
ALTER TABLE `stream_progression`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=69;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `user_roadmaps`
--
ALTER TABLE `user_roadmaps`
  MODIFY `roadmap_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=64;

--
-- AUTO_INCREMENT for table `user_roadmap_steps`
--
ALTER TABLE `user_roadmap_steps`
  MODIFY `step_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=327;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `ai_chat_history`
--
ALTER TABLE `ai_chat_history`
  ADD CONSTRAINT `ai_chat_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `education_level_exams`
--
ALTER TABLE `education_level_exams`
  ADD CONSTRAINT `education_level_exams_ibfk_1` FOREIGN KEY (`education_level_id`) REFERENCES `education_levels` (`education_level_id`),
  ADD CONSTRAINT `education_level_exams_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `entrance_exams` (`exam_id`);

--
-- Constraints for table `education_level_jobs`
--
ALTER TABLE `education_level_jobs`
  ADD CONSTRAINT `education_level_jobs_ibfk_1` FOREIGN KEY (`education_level_id`) REFERENCES `education_levels` (`education_level_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `education_level_jobs_ibfk_2` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `forum_answers`
--
ALTER TABLE `forum_answers`
  ADD CONSTRAINT `forum_answers_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `forum_questions` (`question_id`);

--
-- Constraints for table `forum_answer_likes`
--
ALTER TABLE `forum_answer_likes`
  ADD CONSTRAINT `forum_answer_likes_ibfk_1` FOREIGN KEY (`answer_id`) REFERENCES `forum_answers` (`answer_id`);

--
-- Constraints for table `forum_likes`
--
ALTER TABLE `forum_likes`
  ADD CONSTRAINT `forum_likes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `forum_likes_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `forum_questions` (`question_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `forum_likes_ibfk_3` FOREIGN KEY (`answer_id`) REFERENCES `forum_answers` (`answer_id`) ON DELETE CASCADE;

--
-- Constraints for table `forum_questions`
--
ALTER TABLE `forum_questions`
  ADD CONSTRAINT `forum_questions_ibfk_1` FOREIGN KEY (`education_level_id`) REFERENCES `education_levels` (`education_level_id`);

--
-- Constraints for table `forum_replies`
--
ALTER TABLE `forum_replies`
  ADD CONSTRAINT `forum_replies_ibfk_1` FOREIGN KEY (`answer_id`) REFERENCES `forum_answers` (`answer_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `forum_replies_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`from_user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `roadmaps`
--
ALTER TABLE `roadmaps`
  ADD CONSTRAINT `roadmaps_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `roadmap_steps`
--
ALTER TABLE `roadmap_steps`
  ADD CONSTRAINT `roadmap_steps_ibfk_1` FOREIGN KEY (`roadmap_id`) REFERENCES `roadmaps` (`roadmap_id`);

--
-- Constraints for table `saved_exams`
--
ALTER TABLE `saved_exams`
  ADD CONSTRAINT `saved_exams_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `saved_exams_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `entrance_exams` (`exam_id`);

--
-- Constraints for table `saved_roadmaps`
--
ALTER TABLE `saved_roadmaps`
  ADD CONSTRAINT `saved_roadmaps_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `saved_roadmaps_ibfk_2` FOREIGN KEY (`roadmap_id`) REFERENCES `roadmaps` (`roadmap_id`);

--
-- Constraints for table `streams_old`
--
ALTER TABLE `streams_old`
  ADD CONSTRAINT `streams_old_ibfk_1` FOREIGN KEY (`education_level_id`) REFERENCES `education_levels` (`education_level_id`);

--
-- Constraints for table `stream_exams`
--
ALTER TABLE `stream_exams`
  ADD CONSTRAINT `fk_stream_exams_exam` FOREIGN KEY (`exam_id`) REFERENCES `entrance_exams` (`exam_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_stream_exams_stream` FOREIGN KEY (`stream_id`) REFERENCES `streams` (`stream_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `stream_exams_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `entrance_exams` (`exam_id`);

--
-- Constraints for table `stream_jobs`
--
ALTER TABLE `stream_jobs`
  ADD CONSTRAINT `stream_jobs_ibfk_1` FOREIGN KEY (`stream_id`) REFERENCES `streams` (`stream_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `stream_jobs_ibfk_2` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `stream_progression`
--
ALTER TABLE `stream_progression`
  ADD CONSTRAINT `fk_stream_progression_current` FOREIGN KEY (`current_stream_id`) REFERENCES `streams` (`stream_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_stream_progression_next` FOREIGN KEY (`next_stream_id`) REFERENCES `streams` (`stream_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `user_roadmaps`
--
ALTER TABLE `user_roadmaps`
  ADD CONSTRAINT `user_roadmaps_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `user_roadmap_steps`
--
ALTER TABLE `user_roadmap_steps`
  ADD CONSTRAINT `user_roadmap_steps_ibfk_1` FOREIGN KEY (`roadmap_id`) REFERENCES `user_roadmaps` (`roadmap_id`);

--
-- Constraints for table `user_settings`
--
ALTER TABLE `user_settings`
  ADD CONSTRAINT `user_settings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
