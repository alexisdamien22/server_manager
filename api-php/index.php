<?php

header('Content-Type: application/json');

require_once __DIR__ . '/Router.php';
require_once __DIR__ . '/controllers/ServersController.php';
require_once __DIR__ . '/controllers/HostsController.php';
require_once __DIR__ . '/controllers/GamesController.php';

$router = new Router();

// SERVER ROUTES
$router->get('/api/servers', [ServersController::class, 'list']);
$router->get('/api/servers/:id', [ServersController::class, 'get']);
$router->post('/api/servers', [ServersController::class, 'create']);
$router->delete('/api/servers/:id', [ServersController::class, 'delete']);

// HOST ROUTES
$router->get('/api/hosts', [HostsController::class, 'list']);
$router->get('/api/hosts/:id', [HostsController::class, 'get']);

// GAME ROUTES
$router->get('/api/games', [GamesController::class, 'list']);

$router->dispatch();