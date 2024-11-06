CREATE TABLE device_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,         -- ID unik untuk setiap perangkat (auto-increment)
    user_id BIGINT NOT NULL,                 -- ID pengguna yang memiliki perangkat ini (relasi ke user)
    device_name VARCHAR(100),                -- Nama perangkat, contoh: "iPhone 12"
    device_type VARCHAR(50),                 -- Jenis perangkat, contoh: "Mobile", "Tablet", "Desktop"
    os VARCHAR(50),                          -- Sistem operasi, contoh: "Android", "iOS", "Windows"
    os_version VARCHAR(20),                  -- Versi sistem operasi
    app_version VARCHAR(20),                 -- Versi aplikasi saat login
    device_token VARCHAR(255),               -- Token perangkat untuk notifikasi push (jika diperlukan)
    device_identifier VARCHAR(255) NOT NULL,               -- Device used for login (e.g., mobile, desktop)
    latitude DECIMAL(9, 6),                  -- Latitude saat login
    longitude DECIMAL(9, 6),                 -- Longitude saat login
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- Waktu data dibuat
    last_used TIMESTAMP DEFAULT CURRENT_TIMESTAMP    -- Waktu terakhir digunakan
);

--
--CREATE INDEX idx_user_id
--    on device_info (user_id);
