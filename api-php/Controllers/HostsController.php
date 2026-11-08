
<?php

require_once __DIR__ . '/../db.php';

class HostsController
{
    public static function list(array $params = []): void
    {
        $db = get_db();

        $stmt = $db->query("
            SELECT *
            FROM hosts
            ORDER BY id
        ");

        echo json_encode($stmt->fetchAll());
    }

    public static function get(array $params): void
    {
        $db = get_db();

        $stmt = $db->prepare("
            SELECT *
            FROM hosts
            WHERE id = :id
        ");

        $stmt->execute(['id' => $params['id']]);

        $host = $stmt->fetch();

        if (!$host) {
            http_response_code(404);
            echo json_encode(['error' => 'Host not found']);
            return;
        }

        echo json_encode($host);
    }

    public static function create(array $params = []): void
    {
        $input = json_decode(file_get_contents('php://input'), true);

        if (
            !is_array($input) ||
            empty($input['name']) ||
            empty($input['hostname']) ||
            empty($input['type'])
        ) {
            http_response_code(400);
            echo json_encode(['error' => 'Missing required fields']);
            return;
        }

        if (!in_array($input['type'], ['local', 'remote'], true)) {
            http_response_code(400);
            echo json_encode(['error' => 'Invalid host type']);
            return;
        }

        $connectionType = $input['connection_type']
            ?? ($input['type'] === 'local' ? 'local' : 'ssh');

        $db = get_db();

        $stmt = $db->prepare("
            INSERT INTO hosts (
                name,
                type,
                hostname,
                ip_address,
                operating_system,
                architecture,
                ssh_port,
                connection_type,
                agent_url,
                credential_reference
            )
            VALUES (
                :name,
                :type,
                :hostname,
                :ip_address,
                :operating_system,
                :architecture,
                :ssh_port,
                :connection_type,
                :agent_url,
                :credential_reference
            )
            RETURNING id
        ");

        $stmt->execute([
            'name' => $input['name'],
            'type' => $input['type'],
            'hostname' => $input['hostname'],
            'ip_address' => $input['ip_address'] ?? null,
            'operating_system' => $input['operating_system'] ?? null,
            'architecture' => $input['architecture'] ?? null,
            'ssh_port' => $input['ssh_port'] ?? 22,
            'connection_type' => $connectionType,
            'agent_url' => $input['agent_url'] ?? null,
            'credential_reference' => $input['credential_reference'] ?? null,
        ]);

        http_response_code(201);

        echo json_encode([
            'id' => $stmt->fetchColumn()
        ]);
    }

    public static function update(array $params): void
    {
        $input = json_decode(file_get_contents('php://input'), true);

        if (
            !is_array($input) ||
            empty($input['name']) ||
            empty($input['hostname'])
        ) {
            http_response_code(400);
            echo json_encode(['error' => 'Missing required fields']);
            return;
        }

        $db = get_db();

        $stmt = $db->prepare("
            UPDATE hosts
            SET
                name = :name,
                hostname = :hostname,
                ip_address = :ip_address,
                operating_system = :operating_system,
                status = :status
            WHERE id = :id
            RETURNING id
        ");

        $stmt->execute([
            'id' => $params['id'],
            'name' => $input['name'],
            'hostname' => $input['hostname'],
            'ip_address' => $input['ip_address'] ?? null,
            'operating_system' => $input['operating_system'] ?? null,
            'status' => $input['status'] ?? 'unknown',
        ]);

        if (!$stmt->fetchColumn()) {
            http_response_code(404);
            echo json_encode(['error' => 'Host not found']);
            return;
        }

        echo json_encode(['updated' => true]);
    }

    public static function delete(array $params): void
    {
        $db = get_db();

        $stmt = $db->prepare("
            DELETE FROM hosts
            WHERE id = :id
            RETURNING id
        ");

        $stmt->execute(['id' => $params['id']]);

        if (!$stmt->fetchColumn()) {
            http_response_code(404);
            echo json_encode(['error' => 'Host not found']);
            return;
        }

        echo json_encode(['deleted' => true]);
    }
}
