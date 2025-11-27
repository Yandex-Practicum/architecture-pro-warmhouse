package db

import (
	"context"
	"fmt"

	"github.com/jackc/pgx/v5/pgxpool"
)

// DB represents the database connection
type DB struct {
	Pool *pgxpool.Pool
}

// New creates a new DB instance
func New(connString string) (*DB, error) {
	pool, err := pgxpool.New(context.Background(), connString)
	if err != nil {
		return nil, fmt.Errorf("unable to connect to database: %w", err)
	}

	// Test the connection
	if err := pool.Ping(context.Background()); err != nil {
		return nil, fmt.Errorf("unable to ping database: %w", err)
	}

	return &DB{Pool: pool}, nil
}

// Close closes the database connection
func (db *DB) Close() {
	if db.Pool != nil {
		db.Pool.Close()
	}
}

// InitTables initializes database tables
func (db *DB) InitTables(ctx context.Context) error {
	query := `
		CREATE TABLE IF NOT EXISTS telemetry_data (
			id SERIAL PRIMARY KEY,
			sensor_id INTEGER NOT NULL,
			value FLOAT NOT NULL,
			unit VARCHAR(20),
			status VARCHAR(20) NOT NULL,
			recorded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
			metadata JSONB
		);

		CREATE INDEX IF NOT EXISTS idx_telemetry_sensor_id ON telemetry_data(sensor_id);
		CREATE INDEX IF NOT EXISTS idx_telemetry_recorded_at ON telemetry_data(recorded_at);
	`

	_, err := db.Pool.Exec(ctx, query)
	if err != nil {
		return fmt.Errorf("error creating tables: %w", err)
	}

	return nil
}


