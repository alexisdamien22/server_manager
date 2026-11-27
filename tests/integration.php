<?php

declare(strict_types=1);

$baseApi = 'http://127.0.0.1:8000';
$baseManager = 'http://127.0.0.1:9000';

$agentName = getenv('EXPECTED_AGENT_NAME') ?: 'github-runner';

function request(string $url, string $method = 'GET', array $headers = []): array
{
    $context = stream_context_create([
        'http' => [
            'method' => $method,
            'header' => implode("\r\n", $headers),
            'ignore_errors' => true,
            'timeout' => 3
        ]
    ]);

    $body = @file_get_contents($url, false, $context);

    $status = 0;

    foreach ($http_response_header ?? [] as $header) {
        if (preg_match('/^HTTP\/\S+\s+(\d+)/', $header, $matches)) {
            $status = (int) $matches[1];
        }
    }

    return [
        'status' => $status,
        'body' => $body
    ];
}

function assertTrue(bool $condition, string $message): void
{
    if (!$condition) {
        throw new RuntimeException($message);
    }

    echo "[PASS] $message\n";
}

function waitForService(string $url, int $timeout = 30): void
{
    $deadline = time() + $timeout;

    while (time() < $deadline) {
        $response = request($url);

        if ($response['status'] >= 200 && $response['status'] < 300) {
            return;
        }

        sleep(1);
    }

    throw new RuntimeException(
        "Service did not become available: $url"
    );
}

try {

    // Connect to PostgreSQL

    $pdo = new PDO(
        sprintf(
            'pgsql:host=%s;port=%s;dbname=%s',
            getenv('DB_HOST'),
            getenv('DB_PORT'),
            getenv('DB_NAME')
        ),
        getenv('DB_USER'),
        getenv('DB_PASS'),
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
        ]
    );

    echo "Database connection successful\n";

    // Wait for services

    waitForService("$baseApi/api/hosts");
    assertTrue(true, 'PHP API is healthy');

    waitForService("$baseManager/health");
    assertTrue(true, 'Java Manager is healthy');

    // Verify automatic agent registration

    $statement = $pdo->prepare(
        'SELECT token, last_seen
         FROM agents
         WHERE name = :name'
    );

    $statement->execute([
        'name' => $agentName
    ]);

    $agent = $statement->fetch(PDO::FETCH_ASSOC);

    assertTrue(
        $agent !== false,
        'Agent was registered in PostgreSQL'
    );

    assertTrue(
        !empty($agent['token']),
        'Agent token was generated'
    );

    assertTrue(
        !empty($agent['last_seen']),
        'Agent last_seen was initialized'
    );

    $lastSeenBefore = $agent['last_seen'];

    // Verify heartbeat

    sleep(2);

    $heartbeat = request(
        "$baseApi/api/agents/heartbeat",
        'POST',
        [
            'Authorization: Bearer ' . $agent['token']
        ]
    );

    assertTrue(
        $heartbeat['status'] >= 200 &&
        $heartbeat['status'] < 300,
        'Agent heartbeat was accepted'
    );

    // Verify database update

    $statement->execute([
        'name' => $agentName
    ]);

    $updatedAgent = $statement->fetch(PDO::FETCH_ASSOC);

    assertTrue(
        $updatedAgent !== false &&
        $updatedAgent['last_seen'] !== $lastSeenBefore,
        'Agent last_seen was updated'
    );

    echo "\nAll integration tests passed!\n";

} catch (Throwable $exception) {

    fwrite(
        STDERR,
        "\nIntegration test failed: "
        . $exception->getMessage()
        . "\n"
    );

    exit(1);
}