-- Create the database if it doesn't exist
CREATE DATABASE orders;

-- Connect to the database
\c orders;

-- Create the orders table
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    item VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'created'
);


CREATE DATABASE logistics;
\c logistics;

-- Create the orders table
CREATE TABLE IF NOT EXISTS deliveries (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'initiated'
);

-- Create indexes for common queries
CREATE INDEX IF NOT EXISTS idx_deliveries_order ON deliveries(order_id);
