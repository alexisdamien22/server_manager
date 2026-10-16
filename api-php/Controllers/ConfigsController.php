<?php

require_once __DIR__ . '/../db.php';

class ConfigsController
{
    public static function list(array $params): void
    {
        $db = get_db();
        $stmt = $db->prepare("
            SELECT id, file_path, updated_at
            FROM server_configs
            WHERE server_id = :server_id
            ORDER BY id
        ");
        $stmt->execute(['server_id' => $params['id']]);

        echo json_encode($stmt->fetchAll());
    }

    public static function create(array $params): void
    {
        $input = json_decode(file_get_contents('php://input'), true);

        if (!isset($input['file_path']) || !isset($input['content'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Missing file_path or content']);
            return;
        }

        $db = get_db();
        $stmt = $db->prepare("
            INSERT INTO server_configs (server_id, file_path, content, updated_at)
            VALUES (:server_id, :file_path, :content, NOW())
            RETURNING id
        ");
        $stmt->execute([
            'server_id' => $params['id'],
            'file_path' => $input['file_path'],
            'content' => $input['content']
        ]);

        echo json_encode(['id' => $stmt->fetchColumn()]);
    }

    public static function get(array $params): void
    {
        $db = get_db();
        $stmt = $db->prepare("
            SELECT id, server_id, file_path, content, updated_at
            FROM server_configs
            WHERE id = :config_id
        ");
        $stmt->execute(['config_id' => $params['config_id']]);

        $config = $stmt->fetch();

        if (!$config) {
            http_response_code(404);
            echo json_encode(['error' => 'Config not found']);
            return;
        }

        echo json_encode($config);
    }

    public static function update(array $params): void
    {
        $input = json_decode(file_get_contents('php://input'), true);

        if (!isset($input['content'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Missing content']);
            return;
        }

        $db = get_db();
        $stmt = $db->prepare("
            UPDATE server_configs
            SET content = :content, updated_at = NOW()
            WHERE id = :config_id
            RETURNING id
        ");
        $stmt->execute([
            'content' => $input['content'],
            'config_id' => $params['config_id']
        ]);

        echo json_encode(['updated' => true]);
    }

    public static function delete(array $params): void
    {
        $db = get_db();
        $stmt = $db->prepare("DELETE FROM server_configs WHERE id = :config_id");
        $stmt->execute(['config_id' => $params['config_id']]);

        echo json_encode(['deleted' => true]);
    }
}
