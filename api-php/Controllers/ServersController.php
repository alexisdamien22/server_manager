<?php

require_once __DIR__ . '/../db.php';

class ServersController
{
    public static function list(): void
    {
        $db = get_db();
        $stmt = $db->query("
            SELECT s.id, s.name, s.status,
                   s.internal_port, s.public_port,
                   h.name AS host_name,
                   g.name AS game_name
            FROM servers s
            JOIN hosts h ON s.host_id = h.id
            JOIN games g ON s.game_id = g.id
            ORDER BY s.id
        ");

        echo json_encode($stmt->fetchAll());
    }

    public static function get(array $params): void
    {
        $db = get_db();
        $stmt = $db->prepare("
            SELECT s.*, h.name AS host_name, g.name AS game_name
            FROM servers s
            JOIN hosts h ON s.host_id = h.id
            JOIN games g ON s.game_id = g.id
            WHERE s.id = :id
        ");
        $stmt->execute(['id' => $params['id']]);

        $server = $stmt->fetch();

        if (!$server) {
            http_response_code(404);
            echo json_encode(['error' => 'Server not found']);
            return;
        }

        echo json_encode($server);
    }

    public static function create(): void
    {
        $input = json_decode(file_get_contents('php://input'), true);

        $db = get_db();
        $stmt = $db->prepare("
            INSERT INTO servers (host_id, game_id, name, status, internal_port, public_port, install_path, config_path)
            VALUES (:host_id, :game_id, :name, 'stopped', :internal_port, :public_port, :install_path, :config_path)
            RETURNING id
        ");

        $stmt->execute([
            'host_id' => $input['host_id'],
            'game_id' => $input['game_id'],
            'name' => $input['name'],
            'internal_port' => $input['internal_port'],
            'public_port' => $input['public_port'],
            'install_path' => $input['install_path'],
            'config_path' => $input['config_path'],
        ]);

        echo json_encode(['id' => $stmt->fetchColumn()]);
    }

    public static function delete(array $params): void
    {
        $db = get_db();
        $stmt = $db->prepare("DELETE FROM servers WHERE id = :id");
        $stmt->execute(['id' => $params['id']]);

        echo json_encode(['deleted' => true]);
    }
}