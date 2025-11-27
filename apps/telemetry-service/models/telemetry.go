package models

import (
	"time"
)

// TelemetryData represents a telemetry data record
type TelemetryData struct {
	ID         int                    `json:"id"`
	SensorID   int                    `json:"sensor_id"`
	Value      float64                `json:"value"`
	Unit       string                 `json:"unit"`
	Status     string                 `json:"status"`
	RecordedAt time.Time              `json:"recorded_at"`
	Metadata   map[string]interface{} `json:"metadata"`
}

// TelemetryCollectionRequest represents the request to collect telemetry
type TelemetryCollectionRequest struct {
	SensorIDs []int  `json:"sensor_ids" binding:"required"`
	Force     bool   `json:"force"`
}

// TelemetryCollectionResponse represents the response after collecting telemetry
type TelemetryCollectionResponse struct {
	Collected int       `json:"collected"`
	Timestamp time.Time `json:"timestamp"`
}


