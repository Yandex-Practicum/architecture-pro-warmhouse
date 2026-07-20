package services

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"
)

type DeviceServiceClient struct {
	BaseURL    string
	HTTPClient *http.Client
}

type DeviceRequest struct {
	SensorID int    `json:"sensor_id"`
	Name     string `json:"name"`
	Type     string `json:"type"`
	Location string `json:"location"`
	Unit     string `json:"unit"`
}

type DeviceResponse struct {
	ID        int       `json:"id"`
	SensorID  int       `json:"sensor_id"`
	Name      string    `json:"name"`
	Type      string    `json:"type"`
	Location  string    `json:"location"`
	Unit      string    `json:"unit"`
	Status    string    `json:"status"`
	CreatedAt string    `json:"created_at"`
	UpdatedAt string    `json:"updated_at"`
}

func NewDeviceServiceClient(baseURL string) *DeviceServiceClient {
	return &DeviceServiceClient{
		BaseURL:    baseURL,
		HTTPClient: &http.Client{Timeout: 10 * time.Second},
	}
}

func (c *DeviceServiceClient) CreateDevice(device DeviceRequest) (*DeviceResponse, error) {
	body, err := json.Marshal(device)
	if err != nil {
		return nil, fmt.Errorf("error marshaling device request: %w", err)
	}

	resp, err := c.HTTPClient.Post(fmt.Sprintf("%s/devices", c.BaseURL), "application/json", bytes.NewReader(body))
	if err != nil {
		return nil, fmt.Errorf("error creating device: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated && resp.StatusCode != http.StatusOK {
		respBody, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("device service returned %d: %s", resp.StatusCode, string(respBody))
	}

	var deviceResp DeviceResponse
	if err := json.NewDecoder(resp.Body).Decode(&deviceResp); err != nil {
		return nil, fmt.Errorf("error decoding device response: %w", err)
	}

	return &deviceResp, nil
}

func (c *DeviceServiceClient) UpdateDevice(deviceID int, updates map[string]interface{}) (*DeviceResponse, error) {
	body, err := json.Marshal(updates)
	if err != nil {
		return nil, fmt.Errorf("error marshaling device update: %w", err)
	}

	req, err := http.NewRequest(http.MethodPut, fmt.Sprintf("%s/devices/%d", c.BaseURL, deviceID), bytes.NewReader(body))
	if err != nil {
		return nil, fmt.Errorf("error creating update request: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := c.HTTPClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("error updating device: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		respBody, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("device service returned %d: %s", resp.StatusCode, string(respBody))
	}

	var deviceResp DeviceResponse
	if err := json.NewDecoder(resp.Body).Decode(&deviceResp); err != nil {
		return nil, fmt.Errorf("error decoding device response: %w", err)
	}

	return &deviceResp, nil
}

func (c *DeviceServiceClient) DeleteDevice(deviceID int) error {
	req, err := http.NewRequest(http.MethodDelete, fmt.Sprintf("%s/devices/%d", c.BaseURL, deviceID), nil)
	if err != nil {
		return fmt.Errorf("error creating delete request: %w", err)
	}

	resp, err := c.HTTPClient.Do(req)
	if err != nil {
		return fmt.Errorf("error deleting device: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK && resp.StatusCode != http.StatusNoContent {
		respBody, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("device service returned %d: %s", resp.StatusCode, string(respBody))
	}

	return nil
}

func (c *DeviceServiceClient) GetDeviceBySensorID(sensorID int) (*DeviceResponse, error) {
	resp, err := c.HTTPClient.Get(fmt.Sprintf("%s/devices/sensor/%d", c.BaseURL, sensorID))
	if err != nil {
		return nil, fmt.Errorf("error fetching device: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusNotFound {
		return nil, fmt.Errorf("device not found")
	}
	if resp.StatusCode != http.StatusOK {
		respBody, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("device service returned %d: %s", resp.StatusCode, string(respBody))
	}

	var deviceResp DeviceResponse
	if err := json.NewDecoder(resp.Body).Decode(&deviceResp); err != nil {
		return nil, fmt.Errorf("error decoding device response: %w", err)
	}

	return &deviceResp, nil
}
