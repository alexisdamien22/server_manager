
<?php

use PHPUnit\Framework\TestCase;

class HostsTest extends TestCase
{
    private string $baseUrl = 'http://127.0.0.1:8000/api/hosts';

    private function request(
        string $method,
        string $url,
        ?array $data = null
    ): array {
        $options = [
            'http' => [
                'method' => $method,
                'ignore_errors' => true,
                'header' => "Content-Type: application/json\r\n"
            ]
        ];

        if ($data !== null) {
            $options['http']['content'] = json_encode($data);
        }

        $context = stream_context_create($options);

        $response = file_get_contents(
            $url,
            false,
            $context
        );

        $statusLine = $http_response_header[0] ?? '';

        preg_match('/\s(\d{3})\s/', $statusLine, $matches);

        return [
            'status' => (int) ($matches[1] ?? 0),
            'body' => json_decode($response ?: '', true)
        ];
    }

    public function testHostCrud(): void
    {
        // CREATE

        $created = $this->request(
            'POST',
            $this->baseUrl,
            [
                'name' => 'CI Test Host',
                'type' => 'remote',
                'hostname' => 'test.example.local',
                'ip_address' => '192.168.1.100',
                'operating_system' => 'linux',
                'architecture' => 'x86_64',
                'ssh_port' => 22,
                'connection_type' => 'ssh'
            ]
        );

        $this->assertSame(201, $created['status']);
        $this->assertNotEmpty($created['body']['id']);

        $id = $created['body']['id'];

        // READ

        $retrieved = $this->request(
            'GET',
            $this->baseUrl . '/' . $id
        );

        $this->assertSame(200, $retrieved['status']);
        $this->assertSame(
            'CI Test Host',
            $retrieved['body']['name']
        );

        $this->assertSame(
            'remote',
            $retrieved['body']['type']
        );

        // UPDATE

        $updated = $this->request(
            'PUT',
            $this->baseUrl . '/' . $id,
            [
                'name' => 'Updated CI Host',
                'hostname' => 'updated.example.local',
                'ip_address' => '192.168.1.101',
                'operating_system' => 'linux',
                'status' => 'online'
            ]
        );

        $this->assertSame(200, $updated['status']);
        $this->assertTrue($updated['body']['updated']);

        // VERIFY UPDATE

        $retrieved = $this->request(
            'GET',
            $this->baseUrl . '/' . $id
        );

        $this->assertSame(
            'Updated CI Host',
            $retrieved['body']['name']
        );

        // DELETE

        $deleted = $this->request(
            'DELETE',
            $this->baseUrl . '/' . $id
        );

        $this->assertSame(200, $deleted['status']);
        $this->assertTrue($deleted['body']['deleted']);

        // VERIFY DELETION

        $retrieved = $this->request(
            'GET',
            $this->baseUrl . '/' . $id
        );

        $this->assertSame(404, $retrieved['status']);
    }
}