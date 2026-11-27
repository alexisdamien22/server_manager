<?php

class AgentsController
{
    public function register(): void
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

    public function heartbeat(): void
    {
        $token =
            $this->extractBearerToken();
        
        $db = get_db();
        $statement =
            $db->prepare(
                '
                UPDATE agents
                SET last_seen = NOW()
                WHERE token = :token
                '
            );

        $statement->execute([
            'token' => $token
        ]);

        echo json_encode([
            'status' => 'ok'
        ]);
    }
}