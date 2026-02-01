package db

import (
	"context"
	"errors"
	"fmt"
	"time"

	"smarthome/models"

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

// GetSensors retrieves all sensors from the database
func (db *DB) GetSensors(ctx context.Context) ([]models.Sensor, error) {
	query := `
		SELECT id, name, type, location, value, unit, status, last_updated, created_at
		FROM sensors
		ORDER BY id
	`

	rows, err := db.Pool.Query(ctx, query)
	if err != nil {
		return nil, fmt.Errorf("error querying sensors: %w", err)
	}
	defer rows.Close()

	var sensors []models.Sensor
	for rows.Next() {
		var s models.Sensor
		err := rows.Scan(
			&s.ID,
			&s.Name,
			&s.Type,
			&s.Location,
			&s.Value,
			&s.Unit,
			&s.Status,
			&s.LastUpdated,
			&s.CreatedAt,
		)
		if err != nil {
			return nil, fmt.Errorf("error scanning sensor row: %w", err)
		}
		sensors = append(sensors, s)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("error iterating sensor rows: %w", err)
	}

	return sensors, nil
}

// GetSensorByID retrieves a sensor by its ID
func (db *DB) GetSensorByID(ctx context.Context, id int) (models.Sensor, error) {
	query := `
		SELECT id, name, type, location, value, unit, status, last_updated, created_at
		FROM sensors
		WHERE id = $1
	`

	var s models.Sensor
	err := db.Pool.QueryRow(ctx, query, id).Scan(
		&s.ID,
		&s.Name,
		&s.Type,
		&s.Location,
		&s.Value,
		&s.Unit,
		&s.Status,
		&s.LastUpdated,
		&s.CreatedAt,
	)
	if err != nil {
		return models.Sensor{}, fmt.Errorf("error getting sensor by ID: %w", err)
	}

	return s, nil
}

// CreateSensor creates a new sensor in the database
func (db *DB) CreateSensor(ctx context.Context, s models.SensorCreate) (models.Sensor, error) {
	query := `
		INSERT INTO sensors (name, type, location, unit, status, last_updated, created_at)
		VALUES ($1, $2, $3, $4, 'inactive', $5, $5)
		RETURNING id, name, type, location, value, unit, status, last_updated, created_at
	`

	now := time.Now()
	var sensor models.Sensor
	err := db.Pool.QueryRow(ctx, query,
		s.Name,
		s.Type,
		s.Location,
		s.Unit,
		now,
	).Scan(
		&sensor.ID,
		&sensor.Name,
		&sensor.Type,
		&sensor.Location,
		&sensor.Value,
		&sensor.Unit,
		&sensor.Status,
		&sensor.LastUpdated,
		&sensor.CreatedAt,
	)
	if err != nil {
		return models.Sensor{}, fmt.Errorf("error creating sensor: %w", err)
	}

	return sensor, nil
}

// UpdateSensor updates an existing sensor
func (db *DB) UpdateSensor(ctx context.Context, id int, s models.SensorUpdate) (models.Sensor, error) {
	// First check if the sensor exists
	_, err := db.GetSensorByID(ctx, id)
	if err != nil {
		return models.Sensor{}, err
	}

	// Build the update query dynamically based on which fields are provided
	query := "UPDATE sensors SET last_updated = $1"
	args := []interface{}{time.Now()}
	argCount := 2

	if s.Name != "" {
		query += fmt.Sprintf(", name = $%d", argCount)
		args = append(args, s.Name)
		argCount++
	}

	if s.Type != "" {
		query += fmt.Sprintf(", type = $%d", argCount)
		args = append(args, s.Type)
		argCount++
	}

	if s.Location != "" {
		query += fmt.Sprintf(", location = $%d", argCount)
		args = append(args, s.Location)
		argCount++
	}

	if s.Value != nil {
		query += fmt.Sprintf(", value = $%d", argCount)
		args = append(args, *s.Value)
		argCount++
	}

	if s.Unit != "" {
		query += fmt.Sprintf(", unit = $%d", argCount)
		args = append(args, s.Unit)
		argCount++
	}

	if s.Status != "" {
		query += fmt.Sprintf(", status = $%d", argCount)
		args = append(args, s.Status)
		argCount++
	}

	// Add the WHERE clause and RETURNING clause
	query += ` WHERE id = $` + fmt.Sprintf("%d", argCount) + `
		RETURNING id, name, type, location, value, unit, status, last_updated, created_at`
	args = append(args, id)

	var sensor models.Sensor
	err = db.Pool.QueryRow(ctx, query, args...).Scan(
		&sensor.ID,
		&sensor.Name,
		&sensor.Type,
		&sensor.Location,
		&sensor.Value,
		&sensor.Unit,
		&sensor.Status,
		&sensor.LastUpdated,
		&sensor.CreatedAt,
	)
	if err != nil {
		return models.Sensor{}, fmt.Errorf("error updating sensor: %w", err)
	}

	return sensor, nil
}

// DeleteSensor deletes a sensor by its ID
func (db *DB) DeleteSensor(ctx context.Context, id int) error {
	query := "DELETE FROM sensors WHERE id = $1"
	result, err := db.Pool.Exec(ctx, query, id)
	if err != nil {
		return fmt.Errorf("error deleting sensor: %w", err)
	}

	if result.RowsAffected() == 0 {
		return errors.New("sensor not found")
	}

	return nil
}

// UpdateSensorValue updates the value and status of a sensor
func (db *DB) UpdateSensorValue(ctx context.Context, id int, value float64, status string) error {
	query := `
		UPDATE sensors
		SET value = $1, status = $2, last_updated = $3
		WHERE id = $4
	`

	result, err := db.Pool.Exec(ctx, query, value, status, time.Now(), id)
	if err != nil {
		return fmt.Errorf("error updating sensor value: %w", err)
	}

	if result.RowsAffected() == 0 {
		return errors.New("sensor not found")
	}

	return nil
}
