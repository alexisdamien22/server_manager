<?php

require_once __DIR__ . '/../db.php';

class HostsController
{
    public static function list(): void
    {
        $db = get_db();
        $stmt = $db->query("SELECT * FROM hosts ORDER BY id");
        echo json_encode($stmt->fetchAll());
    }

    public static function get(array $params): void
    {
        $db = get_db();
        $stmt = $db->prepare("SELECT * FROM hosts WHERE id = :id");
        $stmt->execute(['id' => $params['id']]);

        echo json_encode($stmt->fetch());
    }
}