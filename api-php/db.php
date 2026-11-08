<?php

function get_db(): PDO
{
    static $pdo = null;

    if ($pdo !== null) {
        return $pdo;
    }
    
    $config = require __DIR__ . '/config.php';
    $db = $config['db'];

    foreach (['host', 'port', 'name', 'user', 'password'] as $key) {
        if (!isset($db[$key]) || $db[$key] === '') {
            throw new RuntimeException(
                "Missing database configuration: DB_" . strtoupper($key)
            );
        }
    }

    $dsn = sprintf(
        'pgsql:host=%s;port=%d;dbname=%s',
        $db['host'],
        (int) $db['port'],
        $db['name']
    );

    $pdo = new PDO(
        $dsn,
        $db['user'],
        $db['password'],
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        ]
    );

    return $pdo;
}