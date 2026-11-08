
<?php

use PHPUnit\Framework\TestCase;

require_once __DIR__ . '/../../api-php/db.php';

class DatabaseTest extends TestCase
{
    public function testDatabaseConnection(): void
    {
        $db = get_db();

        $this->assertInstanceOf(PDO::class, $db);

        $result = $db->query('SELECT 1')->fetchColumn();

        $this->assertSame('1', (string) $result);
    }

    public function testRequiredTablesExist(): void
    {
        $db = get_db();

        $tables = [
            'hosts',
            'games',
            'server_software',
            'servers',
            'server_configs',
            'console_logs',
            'metrics',
            'installation_profiles',
            'installation_jobs',
            'download_cache',
            'download_jobs',
            'installed_packages',
            'installation_history'
        ];

        foreach ($tables as $table) {
            $stmt = $db->prepare("
                SELECT EXISTS (
                    SELECT 1
                    FROM information_schema.tables
                    WHERE table_schema = 'public'
                    AND table_name = :table
                )
            ");

            $stmt->execute(['table' => $table]);

            $this->assertTrue(
                $stmt->fetchColumn(),
                "Table $table does not exist"
            );
        }
    }

    public function testDefaultGamesExist(): void
    {
        $db = get_db();

        $stmt = $db->query("
            SELECT COUNT(*)
            FROM games
            WHERE slug IN (
                'minecraft',
                'generic-website',
                'generic-application'
            )
        ");

        $this->assertSame(3, (int) $stmt->fetchColumn());
    }
}