<?php

include("config/db.php");
require_once("authenticate.php");

$user_id = authenticate($conn);

echo json_encode([
  "status" => true,
  "message" => "Authenticated",
  "user_id" => $user_id
]);
