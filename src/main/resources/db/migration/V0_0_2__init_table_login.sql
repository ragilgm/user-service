CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,                       -- Foreign key to reference the user
    device VARCHAR(255) NOT NULL,               -- Device used for login (e.g., mobile, desktop)
    device_identifier VARCHAR(255) NOT NULL,               -- Device used for login (e.g., mobile, desktop)
    latitude DECIMAL(9, 6),                     -- Latitude of the login attempt location
    longitude DECIMAL(9, 6),                    -- Longitude of the login attempt location
    attempt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Time of the login attempt
    success TINYINT                     -- Indicates whether the login attempt was successful
);


CREATE TABLE login_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,                       -- Foreign key to reference the user
    device VARCHAR(255) NOT NULL,               -- Device used for login
    latitude DECIMAL(9, 6),                     -- Latitude of the login location
    longitude DECIMAL(9, 6),                    -- Longitude of the login location
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Time of successful login
);


CREATE TABLE otp (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,                       -- Unique identifier for each OTP record
    unique_identifier VARCHAR(100),
    phone_number VARCHAR(100) NOT NULL,                        -- Foreign key to reference the user
    action_type VARCHAR(10),                    -- login , register, change password
    otp_code VARCHAR(6) NOT NULL,               -- The OTP code (e.g., a 6-digit code)
    otp_status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Time when the OTP was generated
    expires_at TIMESTAMP NOT NULL,              -- Expiration time of the OTP
    used TINYINT                             -- Indicates whether the OTP has been used
);
