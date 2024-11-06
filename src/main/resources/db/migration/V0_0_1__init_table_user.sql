CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    uid VARCHAR(100)  NULL,
    username VARCHAR(100)  NOT NULL UNIQUE,
    phone_number VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NULL,
    password_hash VARCHAR(255) NOT NULL,
    transaction_password_hash VARCHAR(255),
    is_active TINYINT,
    user_status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
--
--CREATE INDEX idx_phone_number
--    on users (phone_number);
--
--CREATE INDEX idx_username
--    on users (username);
--
--CREATE INDEX idx_is_active
--    on users (is_active);
--
--CREATE INDEX idx_created_at
--    on users (created_at);
