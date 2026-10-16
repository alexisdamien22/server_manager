<?php

namespace Dotenv;

class Parser
{
    public static function parseLine(string $line): ?array
    {
        if (!str_contains($line, '=')) {
            return null;
        }

        [$key, $value] = explode('=', $line, 2);

        $key = trim($key);
        $value = trim($value);

        if ((str_starts_with($value, '"') && str_ends_with($value, '"')) ||
            (str_starts_with($value, "'") && str_ends_with($value, "'"))) {
            $value = substr($value, 1, -1);
        }

        return [
            'key' => $key,
            'value' => $value
        ];
    }
}