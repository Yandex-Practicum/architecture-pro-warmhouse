-- Create the database if it doesn't exist
CREATE DATABASE deviceservice;

-- Connect to the database
\c deviceservice;

CREATE TABLE IF NOT EXISTS devices (
    id SERIAL PRIMARY KEY,
    house_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    location VARCHAR(100) NOT NULL,
    mac_address VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    heartbeat_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Create indexes for common queries
CREATE INDEX IF NOT EXISTS idx_user_id ON devices(house_id);
CREATE INDEX IF NOT EXISTS idx_devices_location ON devices(location);
CREATE INDEX IF NOT EXISTS idx_devices_mac_address ON devices(mac_address);

CREATE TABLE IF NOT EXISTS device_heartbeats (
    device_id INTEGER PRIMARY KEY,
    heartbeat_at TIMESTAMP WITH TIME ZONE
);
