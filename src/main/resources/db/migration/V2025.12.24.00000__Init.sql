-- Thiết lập search_path để Postgres biết phải làm việc trong schema 'public'
SET search_path = public;

-- EXTENSION: Kích hoạt TimescaleDB cho time-series data
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;

-- BẢNG USERS - Lưu trữ thông tin người dùng
CREATE TABLE IF NOT EXISTS USERS (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE')),
    date_of_birth DATE,
    weight_kg DOUBLE PRECISION,
    height_m DOUBLE PRECISION,
    bmi DOUBLE PRECISION,
    user_role VARCHAR(20) NOT NULL CHECK (user_role IN ('USER', 'ADMIN', 'MANAGER')),
    profile_picture_url TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Index cho tìm kiếm nhanh theo username và email
CREATE INDEX IF NOT EXISTS idx_users_username ON USERS(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON USERS(email);
CREATE INDEX IF NOT EXISTS idx_users_enabled ON USERS(enabled);

-- BẢNG DEVICES - Lưu trữ thông tin thiết bị
CREATE TABLE IF NOT EXISTS DEVICES (
                                       id BIGSERIAL PRIMARY KEY,
                                       device_uuid VARCHAR(255) UNIQUE NOT NULL,
    device_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_devices_user FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE
    );

-- Index cho tìm kiếm nhanh theo device_uuid và user_id
CREATE INDEX IF NOT EXISTS idx_devices_uuid ON DEVICES(device_uuid);
CREATE INDEX IF NOT EXISTS idx_devices_user_id ON DEVICES(user_id);
CREATE INDEX IF NOT EXISTS idx_devices_active ON DEVICES(is_active);

-- BẢNG HEALTH_DATA - Lưu trữ dữ liệu sức khỏe
CREATE TABLE IF NOT EXISTS HEALTH_DATA (
                                           id BIGINT NOT NULL,
                                           device_id BIGINT NOT NULL,
                                           timestamp TIMESTAMP NOT NULL,
                                           heart_rate INTEGER,
                                           steps_count INTEGER,
                                           spo2_percent DOUBLE PRECISION,
                                           PRIMARY KEY (id, timestamp),
    CONSTRAINT fk_health_data_device FOREIGN KEY (device_id) REFERENCES DEVICES(id) ON DELETE CASCADE
    );

-- Chuyển đổi bảng HEALTH_DATA thành hypertable với TimescaleDB
-- Sử dụng timestamp làm time column để tối ưu cho time-series data
SELECT create_hypertable('HEALTH_DATA', 'timestamp',
                         chunk_time_interval => INTERVAL '1 day',
                         if_not_exists => TRUE
       );

-- Index cho tìm kiếm nhanh theo device_id và timestamp
CREATE INDEX IF NOT EXISTS idx_health_data_device_id ON HEALTH_DATA(device_id, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_health_data_timestamp ON HEALTH_DATA(timestamp DESC);

-- Compression policy: Tự động nén dữ liệu cũ hơn 7 ngày
ALTER TABLE HEALTH_DATA SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'device_id'
    );

SELECT add_compression_policy('HEALTH_DATA', INTERVAL '7 days');

-- TRIGGER: Tự động cập nhật updated_at khi có thay đổi
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger cho bảng USERS
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON USERS
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Trigger cho bảng DEVICES
CREATE TRIGGER update_devices_updated_at
    BEFORE UPDATE ON DEVICES
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- DỮ LIỆU MẪU: Tạo tài khoản ADMIN mặc định
-- Password: Password@123 (đã được mã hóa bằng BCrypt)
INSERT INTO USERS (username, password, email, first_name, last_name, user_role, enabled, deleted)
VALUES (
           'admin',
           '$2a$10$EZyJ1ln5LH1Z7xV6E.K05ek7ObFZ9kdMKmfjm39NpAplFAlXJeyWG',
           'admin@lastdance.com',
           'Admin',
           'System',
           'ADMIN',
           TRUE,
           FALSE
       ) ON CONFLICT (username) DO NOTHING;

-- COMMENTS: Mô tả các bảng và cột
COMMENT ON TABLE USERS IS 'Bảng lưu trữ thông tin người dùng hệ thống';
COMMENT ON COLUMN USERS.username IS 'Tên đăng nhập (duy nhất)';
COMMENT ON COLUMN USERS.password IS 'Mật khẩu đã được mã hóa';
COMMENT ON COLUMN USERS.email IS 'Địa chỉ email của người dùng';
COMMENT ON COLUMN USERS.first_name IS 'Tên người dùng';
COMMENT ON COLUMN USERS.last_name IS 'Họ người dùng';
COMMENT ON COLUMN USERS.user_role IS 'Vai trò: USER, ADMIN, MANAGER';
COMMENT ON COLUMN USERS.weight_kg IS 'Cân nặng (kg)';
COMMENT ON COLUMN USERS.height_m IS 'Chiều cao (m)';
COMMENT ON COLUMN USERS.date_of_birth IS 'Ngày sinh của người dùng';
COMMENT ON COLUMN USERS.gender IS 'Giới tính của người dùng';
COMMENT ON COLUMN USERS.bmi IS 'Chỉ số BMI tự động tính toán';
COMMENT ON COLUMN USERS.profile_picture_url IS 'URL hình đại diện người dùng';
COMMENT ON COLUMN USERS.user_role IS 'Vai trò của người dùng trong hệ thống';
COMMENT ON COLUMN USERS.enabled IS 'Trạng thái kích hoạt tài khoản';
COMMENT ON COLUMN USERS.deleted IS 'Đánh dấu tài khoản đã bị xóa';

COMMENT ON TABLE DEVICES IS 'Bảng lưu trữ thông tin thiết bị đeo';
COMMENT ON COLUMN DEVICES.device_uuid IS 'Mã định danh duy nhất của thiết bị';
COMMENT ON COLUMN DEVICES.is_active IS 'Trạng thái hoạt động của thiết bị';
COMMENT ON COLUMN DEVICES.deleted IS 'Đánh dấu thiết bị đã bị xóa';

COMMENT ON TABLE HEALTH_DATA IS 'Bảng lưu trữ dữ liệu sức khỏe từ thiết bị';
COMMENT ON COLUMN HEALTH_DATA.heart_rate IS 'Nhịp tim (bpm)';
COMMENT ON COLUMN HEALTH_DATA.steps_count IS 'Số bước chân';
COMMENT ON COLUMN HEALTH_DATA.spo2_percent IS 'Nồng độ oxy trong máu (%)';
COMMENT ON COLUMN HEALTH_DATA.timestamp IS 'Thời điểm ghi nhận dữ liệu';
COMMENT ON COLUMN HEALTH_DATA.device_id IS 'Tham chiếu đến thiết bị trong bảng DEVICES';

