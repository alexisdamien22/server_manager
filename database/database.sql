
-- ============================================================
-- SERVER_MANAGER
-- PostgreSQL 16+
-- Complete database schema
-- ============================================================

BEGIN;

-- ============================================================
-- 1. HOSTS
-- Local and remote machines
-- ============================================================

CREATE TABLE hosts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    name VARCHAR(255) NOT NULL,

    type VARCHAR(20) NOT NULL
        CHECK (type IN ('local', 'remote')),

    hostname VARCHAR(255) NOT NULL,

    ip_address VARCHAR(45),

    operating_system VARCHAR(30)
        CHECK (
            operating_system IS NULL OR
            operating_system IN ('linux', 'windows', 'macos', 'other')
        ),

    architecture VARCHAR(30),

    ssh_port INTEGER DEFAULT 22
        CHECK (ssh_port BETWEEN 1 AND 65535),

    connection_type VARCHAR(20) NOT NULL DEFAULT 'local'
        CHECK (connection_type IN ('local', 'ssh', 'agent')),

    agent_url TEXT,

    credential_reference VARCHAR(255),

    status VARCHAR(30) NOT NULL DEFAULT 'unknown'
        CHECK (
            status IN ('online', 'offline', 'unknown', 'maintenance')
        ),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CHECK (
        (type = 'local' AND connection_type = 'local')
        OR type = 'remote'
    )
);

-- ============================================================
-- 2. GAMES
-- Supported games and applications
-- ============================================================

CREATE TABLE games (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    name VARCHAR(255) NOT NULL UNIQUE,

    slug VARCHAR(255) UNIQUE,

    description TEXT,

    category VARCHAR(30) NOT NULL DEFAULT 'game'
        CHECK (category IN ('game', 'website', 'application')),

    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 3. SERVER SOFTWARE
-- Vanilla, Paper, Fabric, Forge, etc.
-- ============================================================

CREATE TABLE server_software (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    game_id BIGINT NOT NULL
        REFERENCES games(id) ON DELETE CASCADE,

    name VARCHAR(255) NOT NULL,

    slug VARCHAR(255) NOT NULL,

    description TEXT,

    provider VARCHAR(50),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    UNIQUE (game_id, slug)
);

-- ============================================================
-- 4. SERVERS
-- Managed game servers, websites and applications
-- ============================================================

CREATE TABLE servers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    host_id BIGINT NOT NULL
        REFERENCES hosts(id) ON DELETE RESTRICT,

    game_id BIGINT NOT NULL
        REFERENCES games(id) ON DELETE RESTRICT,

    software_id BIGINT
        REFERENCES server_software(id) ON DELETE SET NULL,

    name VARCHAR(255) NOT NULL,

    server_type VARCHAR(30) NOT NULL DEFAULT 'game'
        CHECK (
            server_type IN ('game', 'website', 'application')
        ),

    status VARCHAR(30) NOT NULL DEFAULT 'stopped'
        CHECK (
            status IN (
                'stopped',
                'starting',
                'running',
                'stopping',
                'restarting',
                'error',
                'installing'
            )
        ),

    internal_port INTEGER
        CHECK (
            internal_port IS NULL OR
            internal_port BETWEEN 1 AND 65535
        ),

    public_port INTEGER
        CHECK (
            public_port IS NULL OR
            public_port BETWEEN 1 AND 65535
        ),

    install_path TEXT NOT NULL,

    config_path TEXT,

    startup_command TEXT,

    memory_limit_mb BIGINT,

    environment JSONB NOT NULL DEFAULT '{}',

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 5. SERVER CONFIGURATIONS
-- ============================================================

CREATE TABLE server_configs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    server_id BIGINT NOT NULL
        REFERENCES servers(id) ON DELETE CASCADE,

    file_path TEXT NOT NULL,

    content TEXT NOT NULL DEFAULT '',

    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    UNIQUE (server_id, file_path)
);

-- ============================================================
-- 6. CONSOLE LOGS
-- ============================================================

CREATE TABLE console_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    server_id BIGINT NOT NULL
        REFERENCES servers(id) ON DELETE CASCADE,

    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),

    message TEXT NOT NULL
);

-- ============================================================
-- 7. METRICS
-- ============================================================

CREATE TABLE metrics (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    server_id BIGINT NOT NULL
        REFERENCES servers(id) ON DELETE CASCADE,

    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),

    cpu_percent NUMERIC(5,2)
        CHECK (
            cpu_percent IS NULL OR
            cpu_percent BETWEEN 0 AND 100
        ),

    ram_mb BIGINT
        CHECK (ram_mb IS NULL OR ram_mb >= 0)
);

-- ============================================================
-- 8. INSTALLATION PROFILES
-- Reusable installation configurations
-- ============================================================

CREATE TABLE installation_profiles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    name VARCHAR(255) NOT NULL,

    description TEXT,

    game_id BIGINT
        REFERENCES games(id) ON DELETE SET NULL,

    software_id BIGINT
        REFERENCES server_software(id) ON DELETE SET NULL,

    installation_type VARCHAR(20) NOT NULL
        CHECK (
            installation_type IN ('automatic', 'manual')
        ),

    configuration JSONB NOT NULL DEFAULT '{}',

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 9. INSTALLATION JOBS
-- ============================================================

CREATE TABLE installation_jobs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    server_id BIGINT
        REFERENCES servers(id) ON DELETE SET NULL,

    host_id BIGINT NOT NULL
        REFERENCES hosts(id) ON DELETE RESTRICT,

    profile_id BIGINT
        REFERENCES installation_profiles(id) ON DELETE SET NULL,

    installation_type VARCHAR(20) NOT NULL
        CHECK (
            installation_type IN ('automatic', 'manual')
        ),

    status VARCHAR(30) NOT NULL DEFAULT 'pending'
        CHECK (
            status IN (
                'pending',
                'queued',
                'downloading',
                'installing',
                'completed',
                'failed',
                'cancelled'
            )
        ),

    progress INTEGER NOT NULL DEFAULT 0
        CHECK (progress BETWEEN 0 AND 100),

    configuration JSONB NOT NULL DEFAULT '{}',

    error_message TEXT,

    started_at TIMESTAMP,

    completed_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 10. DOWNLOAD CACHE
-- Reusable downloaded files
-- ============================================================

CREATE TABLE download_cache (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    provider VARCHAR(50) NOT NULL,

    external_file_id VARCHAR(255),

    file_name VARCHAR(255) NOT NULL,

    file_path TEXT NOT NULL,

    file_size BIGINT
        CHECK (file_size IS NULL OR file_size >= 0),

    hash_algorithm VARCHAR(20),

    file_hash VARCHAR(128),

    download_url TEXT,

    verified BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    last_used_at TIMESTAMP,

    UNIQUE (provider, external_file_id)
);

-- ============================================================
-- 11. DOWNLOAD JOBS
-- ============================================================

CREATE TABLE download_jobs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    installation_id BIGINT NOT NULL
        REFERENCES installation_jobs(id) ON DELETE CASCADE,

    cache_id BIGINT
        REFERENCES download_cache(id) ON DELETE SET NULL,

    provider VARCHAR(50),

    external_project_id VARCHAR(255),

    external_version_id VARCHAR(255),

    external_file_id VARCHAR(255),

    file_name VARCHAR(255) NOT NULL,

    download_url TEXT,

    file_size BIGINT,

    downloaded_bytes BIGINT NOT NULL DEFAULT 0,

    file_hash VARCHAR(128),

    hash_algorithm VARCHAR(20),

    status VARCHAR(30) NOT NULL DEFAULT 'pending'
        CHECK (
            status IN (
                'pending',
                'downloading',
                'verifying',
                'completed',
                'failed',
                'cancelled'
            )
        ),

    error_message TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 12. INSTALLED PACKAGES
-- Mods, plugins, server software and other packages
-- ============================================================

CREATE TABLE installed_packages (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    server_id BIGINT NOT NULL
        REFERENCES servers(id) ON DELETE CASCADE,

    provider VARCHAR(50),

    external_project_id VARCHAR(255),

    external_version_id VARCHAR(255),

    external_file_id VARCHAR(255),

    package_type VARCHAR(30) NOT NULL DEFAULT 'mod'
        CHECK (
            package_type IN (
                'mod',
                'plugin',
                'modpack',
                'server_software',
                'library',
                'other'
            )
        ),

    file_name VARCHAR(255) NOT NULL,

    file_path TEXT NOT NULL,

    file_hash VARCHAR(128),

    installed_at TIMESTAMP NOT NULL DEFAULT NOW(),

    UNIQUE (server_id, file_path)
);

-- ============================================================
-- 13. INSTALLATION HISTORY
-- ============================================================

CREATE TABLE installation_history (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    installation_id BIGINT
        REFERENCES installation_jobs(id) ON DELETE SET NULL,

    server_id BIGINT
        REFERENCES servers(id) ON DELETE SET NULL,

    action VARCHAR(50) NOT NULL,

    details JSONB NOT NULL DEFAULT '{}',

    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 14. INDEXES
-- ============================================================

CREATE INDEX idx_servers_host_id
    ON servers(host_id);

CREATE INDEX idx_servers_game_id
    ON servers(game_id);

CREATE INDEX idx_servers_status
    ON servers(status);

CREATE INDEX idx_configs_server_id
    ON server_configs(server_id);

CREATE INDEX idx_console_server_timestamp
    ON console_logs(server_id, timestamp DESC);

CREATE INDEX idx_metrics_server_timestamp
    ON metrics(server_id, timestamp DESC);

CREATE INDEX idx_installation_jobs_server
    ON installation_jobs(server_id);

CREATE INDEX idx_installation_jobs_status
    ON installation_jobs(status);

CREATE INDEX idx_installation_jobs_host
    ON installation_jobs(host_id);

CREATE INDEX idx_download_jobs_installation
    ON download_jobs(installation_id);

CREATE INDEX idx_download_jobs_status
    ON download_jobs(status);

CREATE INDEX idx_installed_packages_server
    ON installed_packages(server_id);

CREATE INDEX idx_installation_history_server
    ON installation_history(server_id);

CREATE INDEX idx_installation_history_job
    ON installation_history(installation_id);

-- ============================================================
-- 15. DEFAULT UPDATED_AT TRIGGER
-- ============================================================

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_hosts_updated_at
BEFORE UPDATE ON hosts
FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_servers_updated_at
BEFORE UPDATE ON servers
FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_profiles_updated_at
BEFORE UPDATE ON installation_profiles
FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_installation_jobs_updated_at
BEFORE UPDATE ON installation_jobs
FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_download_jobs_updated_at
BEFORE UPDATE ON download_jobs
FOR EACH ROW EXECUTE FUNCTION update_updated_at();

-- ============================================================
-- 16. INITIAL GAME DATA
-- ============================================================

INSERT INTO games (name, slug, description, category)
VALUES
    (
        'Minecraft',
        'minecraft',
        'Minecraft dedicated server',
        'game'
    ),
    (
        'Generic Website',
        'generic-website',
        'Generic hosted website',
        'website'
    ),
    (
        'Generic Application',
        'generic-application',
        'Generic hosted application',
        'application'
    );

COMMIT;

-- ============================================================
-- END OF SCHEMA
-- ============================================================