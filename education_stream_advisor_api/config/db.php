<?php
$host = "127.0.0.1";   // IMPORTANT
$user = "root";
$password = "";
$dbname = "education_stream_advisor_app";
$port = 3307;          // MUST MATCH my.ini

$conn = new mysqli($host, $user, $password, $dbname, $port);

if ($conn->connect_error) {
    die("Database connection failed: " . $conn->connect_error);
}

?>
