<?php

namespace Dotenv;

class Loader
{
    public static function setEnv(string $key, string $value): void
    {
        $_ENV[$key] = $value;

        $_SERVER[$key] = $value;

        putenv("$key=$value");
    }
}
