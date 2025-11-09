-- Create the database if it doesn't exist
CREATE DATABASE telemetryservice;

-- Connect to the database
\c telemetryservice;

CREATE TABLE IF NOT EXISTS metrics (
    id UUID PRIMARY KEY,
    device_id INTEGER NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    value REAL
);

CREATE INDEX IF NOT EXISTS idx_device_id ON metrics(device_id);
