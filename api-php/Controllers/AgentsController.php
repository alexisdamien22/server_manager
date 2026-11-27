<?php

class AgentsController
{
    public static function register(): void
    {
        $body = json_decode(
            file_get_contents(
                'php://input'
            ),
            true
        );

        $token =
            bin2hex(
                random_bytes(32)
            );
        
        $db = get_db();
        $statement =
            $db->prepare(
                '
                INSERT INTO agents
                (
                    name,
                    version,
                    token,
                    last_seen
                )
                VALUES
                (
                    :name,
                    :version,
                    :token,
                    NOW()
                )
                RETURNING id
                '
            );

        $statement->execute([
            'name' => $body['name'],
            'version' => $body['version'],
            'token' => $token
        ]);

        $agentId =
            $statement->fetchColumn();

        echo json_encode([
            'agentId' => $agentId,
            'token' => $token
        ]);
    }

    public static function heartbeat(
        array $params = []
    ): void {

        $authorization =
            $_SERVER['HTTP_AUTHORIZATION']
            ?? '';

        if (!str_starts_with(
            $authorization,
            'Bearer '
        )) {

            http_response_code(401);

            echo json_encode([
                'error' =>
                    'Missing token'
            ]);

            return;
        }

        $token =
            substr(
                $authorization,
                7
            );

        $database =
            get_db();

        $statement =
            $database->prepare(
                '
                UPDATE agents
                SET last_seen = NOW()
                WHERE token = :token
                '
            );

        $statement->execute([
            'token' => $token
        ]);

        if ($statement->rowCount() === 0) {

            http_response_code(401);

            echo json_encode([
                'error' =>
                    'Invalid token'
            ]);

            return;
        }

        echo json_encode([
            'status' => 'ok'
        ]);
    }
}