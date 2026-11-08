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

        $required = ['host_id', 'game_id', 'name', 'internal_port', 'public_port', 'install_path', 'config_path'];
        foreach ($required as $field) {
            if (!isset($input[$field])) {
                http_response_code(400);
                echo json_encode(['error' => "Missing field: $field"]);
                return;
            }
        }

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

    public static function getConsole(array $params): void
    {
        $db = get_db();
        $stmt = $db->prepare("
            SELECT id, timestamp, message
            FROM console_logs
            WHERE server_id = :id
            ORDER BY id DESC
            LIMIT 200
        ");
        $stmt->execute(['id' => $params['id']]);

        echo json_encode($stmt->fetchAll());
    }

    public static function sendConsoleCommand(array $params): void
    {
        $input = json_decode(file_get_contents('php://input'), true);

        if (!isset($input['command'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Missing command']);
            return;
        }

        $url = getenv('JAVA_ORCHESTRATOR_URL') . "/server/" . $params['id'] . "/console";

        $response = file_get_contents($url, false, stream_context_create([
            'http' => [
                'method' => 'POST',
                'header' => "Content-Type: application/json\r\n",
                'content' => json_encode(['command' => $input['command']])
            ]
        ]));

        echo $response ?: json_encode(['status' => 'sent']);
    }

    public static function getMetrics(array $params): void
    {
        $db = get_db();
        $stmt = $db->prepare("
            SELECT timestamp, cpu_percent, ram_mb
            FROM metrics
            WHERE server_id = :id
            ORDER BY timestamp DESC
            LIMIT 50
        ");
        $stmt->execute(['id' => $params['id']]);

        echo json_encode($stmt->fetchAll());
    }
}