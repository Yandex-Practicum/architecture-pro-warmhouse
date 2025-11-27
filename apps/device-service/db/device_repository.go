package db

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"time"

	"device/models"
)

// GetDevices retrieves all devices from the database
func (db *DB) GetDevices(ctx context.Context) ([]models.Device, error) {
	query := `
		SELECT id, name, type, location, status, configuration, last_updated, created_at
		FROM devices
		ORDER BY id
	`

	rows, err := db.Pool.Query(ctx, query)
	if err != nil {
		return nil, fmt.Errorf("error querying devices: %w", err)
	}
	defer rows.Close()

	var devices []models.Device
	for rows.Next() {
		var d models.Device
		var configJSON []byte
		err := rows.Scan(
			&d.ID,
			&d.Name,
			&d.Type,
			&d.Location,
			&d.Status,
			&configJSON,
			&d.LastUpdated,
			&d.CreatedAt,
		)
		if err != nil {
			return nil, fmt.Errorf("error scanning device row: %w", err)
		}

		// Parse configuration JSON
		if len(configJSON) > 0 {
			if err := json.Unmarshal(configJSON, &d.Configuration); err != nil {
				d.Configuration = make(map[string]interface{})
			}
		} else {
			d.Configuration = make(map[string]interface{})
		}

		devices = append(devices, d)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("error iterating device rows: %w", err)
	}

	return devices, nil
}

// GetDeviceByID retrieves a device by its ID
func (db *DB) GetDeviceByID(ctx context.Context, id int) (*models.Device, error) {
	query := `
		SELECT id, name, type, location, status, configuration, last_updated, created_at
		FROM devices
		WHERE id = $1
	`

	var d models.Device
	var configJSON []byte
	err := db.Pool.QueryRow(ctx, query, id).Scan(
		&d.ID,
		&d.Name,
		&d.Type,
		&d.Location,
		&d.Status,
		&configJSON,
		&d.LastUpdated,
		&d.CreatedAt,
	)
	if err != nil {
		return nil, fmt.Errorf("error getting device by ID: %w", err)
	}

	// Parse configuration JSON
	if len(configJSON) > 0 {
		if err := json.Unmarshal(configJSON, &d.Configuration); err != nil {
			d.Configuration = make(map[string]interface{})
		}
	} else {
		d.Configuration = make(map[string]interface{})
	}

	return &d, nil
}

// CreateDevice creates a new device in the database
func (db *DB) CreateDevice(ctx context.Context, d models.DeviceCreate) (*models.Device, error) {
	configJSON, err := json.Marshal(d.Configuration)
	if err != nil {
		configJSON = []byte("{}")
	}

	query := `
		INSERT INTO devices (name, type, location, status, configuration, last_updated, created_at)
		VALUES ($1, $2, $3, 'off', $4, $5, $5)
		RETURNING id, name, type, location, status, configuration, last_updated, created_at
	`

	now := time.Now()
	var device models.Device
	var configJSONResult []byte
	err = db.Pool.QueryRow(ctx, query,
		d.Name,
		d.Type,
		d.Location,
		configJSON,
		now,
	).Scan(
		&device.ID,
		&device.Name,
		&device.Type,
		&device.Location,
		&device.Status,
		&configJSONResult,
		&device.LastUpdated,
		&device.CreatedAt,
	)
	if err != nil {
		return nil, fmt.Errorf("error creating device: %w", err)
	}

	// Parse configuration JSON
	if len(configJSONResult) > 0 {
		if err := json.Unmarshal(configJSONResult, &device.Configuration); err != nil {
			device.Configuration = make(map[string]interface{})
		}
	} else {
		device.Configuration = make(map[string]interface{})
	}

	return &device, nil
}

// UpdateDevice updates an existing device
func (db *DB) UpdateDevice(ctx context.Context, id int, d models.DeviceUpdate) (*models.Device, error) {
	// First check if the device exists
	_, err := db.GetDeviceByID(ctx, id)
	if err != nil {
		return nil, err
	}

	// Build the update query dynamically based on which fields are provided
	query := "UPDATE devices SET last_updated = $1"
	args := []interface{}{time.Now()}
	argCount := 2

	if d.Name != "" {
		query += fmt.Sprintf(", name = $%d", argCount)
		args = append(args, d.Name)
		argCount++
	}

	if d.Type != "" {
		query += fmt.Sprintf(", type = $%d", argCount)
		args = append(args, d.Type)
		argCount++
	}

	if d.Location != "" {
		query += fmt.Sprintf(", location = $%d", argCount)
		args = append(args, d.Location)
		argCount++
	}

	if d.Configuration != nil {
		configJSON, err := json.Marshal(d.Configuration)
		if err != nil {
			configJSON = []byte("{}")
		}
		query += fmt.Sprintf(", configuration = $%d", argCount)
		args = append(args, configJSON)
		argCount++
	}

	// Add the WHERE clause and RETURNING clause
	query += ` WHERE id = $` + fmt.Sprintf("%d", argCount) + `
		RETURNING id, name, type, location, status, configuration, last_updated, created_at`
	args = append(args, id)

	var device models.Device
	var configJSON []byte
	err = db.Pool.QueryRow(ctx, query, args...).Scan(
		&device.ID,
		&device.Name,
		&device.Type,
		&device.Location,
		&device.Status,
		&configJSON,
		&device.LastUpdated,
		&device.CreatedAt,
	)
	if err != nil {
		return nil, fmt.Errorf("error updating device: %w", err)
	}

	// Parse configuration JSON
	if len(configJSON) > 0 {
		if err := json.Unmarshal(configJSON, &device.Configuration); err != nil {
			device.Configuration = make(map[string]interface{})
		}
	} else {
		device.Configuration = make(map[string]interface{})
	}

	return &device, nil
}

// UpdateDeviceState updates the state of a device
func (db *DB) UpdateDeviceState(ctx context.Context, id int, state models.DeviceStatus) (*models.Device, error) {
	query := `
		UPDATE devices
		SET status = $1, last_updated = $2
		WHERE id = $3
		RETURNING id, name, type, location, status, configuration, last_updated, created_at
	`

	var device models.Device
	var configJSON []byte
	err := db.Pool.QueryRow(ctx, query, state, time.Now(), id).Scan(
		&device.ID,
		&device.Name,
		&device.Type,
		&device.Location,
		&device.Status,
		&configJSON,
		&device.LastUpdated,
		&device.CreatedAt,
	)
	if err != nil {
		return nil, fmt.Errorf("error updating device state: %w", err)
	}

	// Parse configuration JSON
	if len(configJSON) > 0 {
		if err := json.Unmarshal(configJSON, &device.Configuration); err != nil {
			device.Configuration = make(map[string]interface{})
		}
	} else {
		device.Configuration = make(map[string]interface{})
	}

	return &device, nil
}

// DeleteDevice deletes a device by its ID
func (db *DB) DeleteDevice(ctx context.Context, id int) error {
	query := "DELETE FROM devices WHERE id = $1"
	result, err := db.Pool.Exec(ctx, query, id)
	if err != nil {
		return fmt.Errorf("error deleting device: %w", err)
	}

	if result.RowsAffected() == 0 {
		return errors.New("device not found")
	}

	return nil
}


