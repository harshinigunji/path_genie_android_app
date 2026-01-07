<?php
header("Content-Type: application/json");
include("response.php");

$data = json_decode(file_get_contents("php://input"), true);

if (!$data) {
    error("Invalid input");
}

$input_json = json_encode($data);

/* FULL PATHS */
$pythonPath = "C:\\Users\\harsh\\AppData\\Local\\Programs\\Python\\Python312\\python.exe";
$scriptPath = __DIR__ . "\\ml\\predict_next_stream.py";

/* COMMAND */
$cmd = "\"$pythonPath\" \"$scriptPath\"";

/* PIPE CONFIG */
$process = proc_open(
    $cmd,
    [
        0 => ["pipe", "r"],
        1 => ["pipe", "w"],
        2 => ["pipe", "w"]
    ],
    $pipes
);

if (!is_resource($process)) {
    error("ML process could not be started");
}

/* SEND INPUT */
fwrite($pipes[0], $input_json);
fclose($pipes[0]);

/* READ OUTPUT */
$output = stream_get_contents($pipes[1]);
fclose($pipes[1]);

/* IGNORE STDERR IN PROD */
fclose($pipes[2]);

proc_close($process);

/* EXTRACT JSON */
preg_match('/\{.*\}/s', $output, $match);

if (!isset($match[0])) {
    error("Invalid ML response");
}

success(json_decode($match[0], true));
