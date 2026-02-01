package models

import (
	"time"
)

// SensorType represents the type of sensor
type SensorType string

const (
	Temperature SensorType = "temperature"
)

// Sensor represents a smart home sensor
type Sensor struct {
	ID          int        `json:"id"`
	Name        string     `json:"name"`
	Type        SensorType `json:"type"`
	Location    string     `json:"location"`
	Value       float64    `json:"value"`
	Unit        string     `json:"unit"`
	Status      string     `json:"status"`
	LastUpdated time.Time  `json:"last_updated"`
	CreatedAt   time.Time  `json:"created_at"`
}

// SensorCreate represents the data needed to create a new sensor
type SensorCreate struct {
	Name     string     `json:"name" binding:"required"`
	Type     SensorType `json:"type" binding:"required"`
	Location string     `json:"location" binding:"required"`
	Unit     string     `json:"unit"`
}

// SensorUpdate represents the data that can be updated for a sensor
type SensorUpdate struct {
	Name     string     `json:"name"`
	Type     SensorType `json:"type"`
	Location string     `json:"location"`
	Value    *float64   `json:"value"`
	Unit     string     `json:"unit"`
	Status   string     `json:"status"`
}
