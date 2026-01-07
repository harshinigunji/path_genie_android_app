<?php
function success($data) {
    echo json_encode([
        "status" => true,
        "data" => $data
    ]);
    exit;
}

function error($message, $code = "GENERAL_ERROR") {
    echo json_encode([
        "status" => false,
        "error_code" => $code,
        "message" => $message
    ]);
    exit;
}
