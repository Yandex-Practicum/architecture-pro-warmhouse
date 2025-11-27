package models

import (
	"time"
)

// DeviceType represents the type of device
type DeviceType string

const (
	Thermostat      DeviceType = "thermostat"
	Heater          DeviceType = "heater"
	AirConditioner DeviceType = "air_conditioner"
	Light           DeviceType = "light"
	Camera          DeviceType = "camera"
)

// DeviceStatus represents the status of a device
type DeviceStatus string

const (
	DeviceOn      DeviceStatus = "on"
	DeviceOff     DeviceStatus = "off"
	DeviceStandby DeviceStatus = "standby"
	DeviceError   DeviceStatus = "error"
)

// Device represents a smart home device
type Device struct {
	ID           int                    `json:"id"`
	Name         string                 `json:"name"`
	Type         DeviceType             `json:"type"`
	Location     string                 `json:"location"`
	Status       DeviceStatus           `json:"status"`
	Configuration map[string]interface{} `json:"configuration"`
	LastUpdated  time.Time              `json:"last_updated"`
	CreatedAt    time.Time              `json:"created_at"`
}

// DeviceCreate represents the data needed to create a new device
type DeviceCreate struct {
	Name         string                 `json:"name" binding:"required"`
	Type         DeviceType             `json:"type" binding:"required"`
	Location     string                 `json:"location" binding:"required"`
	Configuration map[string]interface{} `json:"configuration"`
}

// DeviceUpdate represents the data that can be updated for a device
type DeviceUpdate struct {
	Name         string                 `json:"name"`
	Type         DeviceType             `json:"type"`
	Location     string                 `json:"location"`
	Configuration map[string]interface{} `json:"configuration"`
}

// DeviceStateUpdate represents the data needed to update device state
type DeviceStateUpdate struct {
	State  DeviceStatus `json:"state" binding:"required"`
	Reason string       `json:"reason"`
}


