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
		CREATE TABLE IF NOT EXISTS devices (
			id SERIAL PRIMARY KEY,
			name VARCHAR(100) NOT NULL,
			type VARCHAR(50) NOT NULL,
			location VARCHAR(100) NOT NULL,
			status VARCHAR(20) NOT NULL DEFAULT 'off',
			configuration TEXT,
			last_updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
			created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
		);

		CREATE INDEX IF NOT EXISTS idx_devices_type ON devices(type);
		CREATE INDEX IF NOT EXISTS idx_devices_location ON devices(location);
		CREATE INDEX IF NOT EXISTS idx_devices_status ON devices(status);
	`

	_, err := db.Pool.Exec(ctx, query)
	if err != nil {
		return fmt.Errorf("error creating tables: %w", err)
	}

	return nil
}


