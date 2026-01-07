<?php
header("Content-Type: application/json");

/* ENABLE DEBUG */
error_reporting(E_ALL);
ini_set('display_errors', 1);

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    echo json_encode(["error" => "Invalid input"]);
    exit;
}

$input_json = json_encode($data);

/* FULL PYTHON PATH */
$pythonPath = "C:\\Users\\harsh\\AppData\\Local\\Programs\\Python\\Python312\\python.exe";
$scriptPath = __DIR__ . "\\ml\\predict_next_stream.py";

/* COMMAND */
$cmd = "\"$pythonPath\" \"$scriptPath\"";

/* DESCRIPTORS */
$descriptorspec = [
    0 => ["pipe", "r"],
    1 => ["pipe", "w"],
    2 => ["pipe", "w"]
];

$process = proc_open($cmd, $descriptorspec, $pipes);

if (!is_resource($process)) {
    echo json_encode(["error" => "Process not started"]);
    exit;
}

/* SEND INPUT */
fwrite($pipes[0], $input_json);
fclose($pipes[0]);

/* READ OUTPUTS */
$stdout = stream_get_contents($pipes[1]);
$stderr = stream_get_contents($pipes[2]);

fclose($pipes[1]);
fclose($pipes[2]);

$returnCode = proc_close($process);

/* RETURN EVERYTHING */
echo json_encode([
    "stdout" => $stdout,
    "stderr" => $stderr,
    "return_code" => $returnCode,
    "command" => $cmd
]);
