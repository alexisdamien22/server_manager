<?php

require_once __DIR__ . '/../Dotenv/src/Dotenv.php';
require_once __DIR__ . '/../Dotenv/src/Loader.php';
require_once __DIR__ . '/../Dotenv/src/Parser.php';

$dotenv = new Dotenv\Dotenv(dirname(__DIR__));
$dotenv->load();

return [
    'db' => [
        'host' => getenv('DB_HOST'),
        'port' => getenv('DB_PORT'),
        'name' => getenv('DB_NAME'),
        'user' => getenv('DB_USER'),
        'password' => getenv('DB_PASS'),
    ],
];