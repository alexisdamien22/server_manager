<?php

namespace Dotenv;

require_once __DIR__ . '/Loader.php';
require_once __DIR__ . '/Parser.php';

class Dotenv
{
    private string $path;

    public function __construct(string $path)
    {
        $this->path = rtrim($path, '/');
    }

    public function load(): void
    {
        $file = $this->path . '/.env';

        if (!file_exists($file)) {
            throw new \Exception(".env file not found at: " . $file);
        }

        $lines = file($file, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);

        foreach ($lines as $line) {
            $line = trim($line);

            // Skip comments
            if (str_starts_with($line, '#')) {
                continue;
            }

            $parsed = Parser::parseLine($line);

            if ($parsed !== null) {
                Loader::setEnv($parsed['key'], $parsed['value']);
            }
        }
    }
}