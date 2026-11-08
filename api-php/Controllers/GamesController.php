<?php

require_once __DIR__ . '/../db.php';

class GamesController
{
    public static function list(): void
    {
        $db = get_db();
        $stmt = $db->query("SELECT * FROM games ORDER BY id");
        echo json_encode($stmt->fetchAll());
    }
}