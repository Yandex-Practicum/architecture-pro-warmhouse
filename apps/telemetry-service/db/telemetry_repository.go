package db

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"math/rand"
	"time"

	"telemetry/models"
)

// GetTelemetryBySensor retrieves the latest telemetry data for a sensor
func (db *DB) GetTelemetryBySensor(ctx context.Context, sensorID int) (*models.TelemetryData, error) {
	query := `
		SELECT id, sensor_id, value, unit, status, recorded_at, metadata
		FROM telemetry_data
		WHERE sensor_id = $1
		ORDER BY recorded_at DESC
		LIMIT 1
	`

	var t models.TelemetryData
	var metadataJSON []byte
	err := db.Pool.QueryRow(ctx, query, sensorID).Scan(
		&t.ID,
		&t.SensorID,
		&t.Value,
		&t.Unit,
		&t.Status,
		&t.RecordedAt,
		&metadataJSON,
	)
	if err != nil {
		return nil, fmt.Errorf("error getting telemetry by sensor: %w", err)
	}

	// Parse metadata JSON
	if len(metadataJSON) > 0 {
		if err := json.Unmarshal(metadataJSON, &t.Metadata); err != nil {
			t.Metadata = make(map[string]interface{})
		}
	} else {
		t.Metadata = make(map[string]interface{})
	}

	return &t, nil
}

// GetTelemetryHistory retrieves telemetry history for a sensor
func (db *DB) GetTelemetryHistory(ctx context.Context, sensorID int, from, to *time.Time, limit int) ([]models.TelemetryData, error) {
	query := `
		SELECT id, sensor_id, value, unit, status, recorded_at, metadata
		FROM telemetry_data
		WHERE sensor_id = $1
	`
	args := []interface{}{sensorID}
	argCount := 2

	if from != nil {
		query += fmt.Sprintf(" AND recorded_at >= $%d", argCount)
		args = append(args, *from)
		argCount++
	}

	if to != nil {
		query += fmt.Sprintf(" AND recorded_at <= $%d", argCount)
		args = append(args, *to)
		argCount++
	}

	query += fmt.Sprintf(" ORDER BY recorded_at DESC LIMIT $%d", argCount)
	args = append(args, limit)

	rows, err := db.Pool.Query(ctx, query, args...)
	if err != nil {
		return nil, fmt.Errorf("error querying telemetry history: %w", err)
	}
	defer rows.Close()

	var telemetryList []models.TelemetryData
	for rows.Next() {
		var t models.TelemetryData
		var metadataJSON []byte
		err := rows.Scan(
			&t.ID,
			&t.SensorID,
			&t.Value,
			&t.Unit,
			&t.Status,
			&t.RecordedAt,
			&metadataJSON,
		)
		if err != nil {
			return nil, fmt.Errorf("error scanning telemetry row: %w", err)
		}

		// Parse metadata JSON
		if len(metadataJSON) > 0 {
			if err := json.Unmarshal(metadataJSON, &t.Metadata); err != nil {
				t.Metadata = make(map[string]interface{})
			}
		} else {
			t.Metadata = make(map[string]interface{})
		}

		telemetryList = append(telemetryList, t)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("error iterating telemetry rows: %w", err)
	}

	return telemetryList, nil
}

// CreateTelemetryData creates a new telemetry data record
func (db *DB) CreateTelemetryData(ctx context.Context, sensorID int, value float64, unit, status string, metadata map[string]interface{}) error {
	metadataJSON, err := json.Marshal(metadata)
	if err != nil {
		metadataJSON = []byte("{}")
	}

	query := `
		INSERT INTO telemetry_data (sensor_id, value, unit, status, metadata, recorded_at)
		VALUES ($1, $2, $3, $4, $5, NOW())
	`

	_, err = db.Pool.Exec(ctx, query, sensorID, value, unit, status, metadataJSON)
	if err != nil {
		return fmt.Errorf("error creating telemetry data: %w", err)
	}

	return nil
}

// CollectTelemetry collects telemetry data for multiple sensors
func (db *DB) CollectTelemetry(ctx context.Context, sensorIDs []int) (int, error) {
	if len(sensorIDs) == 0 {
		return 0, errors.New("sensor_ids is required")
	}

	collected := 0
	for _, sensorID := range sensorIDs {
		// Generate random temperature value between 18 and 25
		value := 18.0 + (25.0-18.0)*rand.Float64()
		
		metadata := map[string]interface{}{
			"source":      "telemetry-service",
			"collected_at": time.Now().Format(time.RFC3339),
		}

		if err := db.CreateTelemetryData(ctx, sensorID, value, "Celsius", "active", metadata); err != nil {
			return collected, err
		}
		collected++
	}

	return collected, nil
}

