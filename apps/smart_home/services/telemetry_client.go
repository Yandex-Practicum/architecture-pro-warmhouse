package services

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"
)

type TelemetryServiceClient struct {
	BaseURL    string
	HTTPClient *http.Client
}

type TelemetryReadingRequest struct {
	SensorID int     `json:"sensorId"`
	Value    float64 `json:"value"`
	Unit     string  `json:"unit"`
	Location string  `json:"location"`
}

type TelemetryReadingResponse struct {
	ID        int64     `json:"id"`
	SensorID  int       `json:"sensorId"`
	Value     float64   `json:"value"`
	Unit      string    `json:"unit"`
	Location  string    `json:"location"`
	Timestamp string    `json:"timestamp"`
}

func NewTelemetryServiceClient(baseURL string) *TelemetryServiceClient {
	return &TelemetryServiceClient{
		BaseURL:    baseURL,
		HTTPClient: &http.Client{Timeout: 10 * time.Second},
	}
}

func (c *TelemetryServiceClient) PushReading(reading TelemetryReadingRequest) (*TelemetryReadingResponse, error) {
	body, err := json.Marshal(reading)
	if err != nil {
		return nil, fmt.Errorf("error marshaling telemetry request: %w", err)
	}

	resp, err := c.HTTPClient.Post(fmt.Sprintf("%s/telemetry", c.BaseURL), "application/json", bytes.NewReader(body))
	if err != nil {
		return nil, fmt.Errorf("error pushing telemetry reading: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		respBody, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("telemetry service returned %d: %s", resp.StatusCode, string(respBody))
	}

	var readingResp TelemetryReadingResponse
	if err := json.NewDecoder(resp.Body).Decode(&readingResp); err != nil {
		return nil, fmt.Errorf("error decoding telemetry response: %w", err)
	}

	return &readingResp, nil
}

func (c *TelemetryServiceClient) GetLatestReading(sensorID int) (*TelemetryReadingResponse, error) {
	resp, err := c.HTTPClient.Get(fmt.Sprintf("%s/telemetry/sensor/%d/latest", c.BaseURL, sensorID))
	if err != nil {
		return nil, fmt.Errorf("error fetching latest reading: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusNotFound {
		return nil, fmt.Errorf("no readings found for sensor %d", sensorID)
	}
	if resp.StatusCode != http.StatusOK {
		respBody, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("telemetry service returned %d: %s", resp.StatusCode, string(respBody))
	}

	var readingResp TelemetryReadingResponse
	if err := json.NewDecoder(resp.Body).Decode(&readingResp); err != nil {
		return nil, fmt.Errorf("error decoding telemetry response: %w", err)
	}

	return &readingResp, nil
}

func (c *TelemetryServiceClient) GetReadingsBySensor(sensorID int) ([]TelemetryReadingResponse, error) {
	resp, err := c.HTTPClient.Get(fmt.Sprintf("%s/telemetry/sensor/%d", c.BaseURL, sensorID))
	if err != nil {
		return nil, fmt.Errorf("error fetching readings: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		respBody, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("telemetry service returned %d: %s", resp.StatusCode, string(respBody))
	}

	var readings []TelemetryReadingResponse
	if err := json.NewDecoder(resp.Body).Decode(&readings); err != nil {
		return nil, fmt.Errorf("error decoding telemetry response: %w", err)
	}

	return readings, nil
}
