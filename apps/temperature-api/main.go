package main

import (
	"encoding/json"
	"fmt"
	"log"
	"math/rand"
	"net/http"
	"strings"
	"time"
)

var locationSensorMap = map[string]string{
	"Living Room": "1",
	"Bedroom":     "2",
	"Kitchen":     "3",
}

var sensorLocationMap = map[string]string{
	"1": "Living Room",
	"2": "Bedroom",
	"3": "Kitchen",
}

type TemperatureResponse struct {
	Value       float64   `json:"value"`
	Unit        string    `json:"unit"`
	Timestamp   time.Time `json:"timestamp"`
	Location    string    `json:"location"`
	Status      string    `json:"status"`
	SensorID    string    `json:"sensor_id"`
	SensorType  string    `json:"sensor_type"`
	Description string    `json:"description"`
}

func resolveLocation(location, sensorID string) (string, string) {
	if location == "" {
		switch sensorID {
		case "1":
			location = "Living Room"
		case "2":
			location = "Bedroom"
		case "3":
			location = "Kitchen"
		default:
			location = "Unknown"
		}
	}

	if sensorID == "" {
		lower := strings.ToLower(location)
		for loc, id := range locationSensorMap {
			if strings.ToLower(loc) == lower {
				sensorID = id
				break
			}
		}
		if sensorID == "" {
			sensorID = "0"
		}
	}

	return location, sensorID
}

func randomTemperature() float64 {
	return 18.0 + rand.Float64()*8.0
}

func handleTemperature(w http.ResponseWriter, r *http.Request) {
	location := r.URL.Query().Get("location")

	sensorID := ""
	for _, segment := range strings.Split(r.URL.Path, "/") {
		if segment != "temperature" && segment != "" {
			sensorID = segment
			break
		}
	}

	location, sensorID = resolveLocation(location, sensorID)

	value := randomTemperature()
	unit := "°C"
	status := "active"
	sensorType := "temperature"
	description := fmt.Sprintf("Temperature reading for %s", location)

	resp := TemperatureResponse{
		Value:       value,
		Unit:        unit,
		Timestamp:   time.Now().UTC(),
		Location:    location,
		Status:      status,
		SensorID:    sensorID,
		SensorType:  sensorType,
		Description: description,
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
}

func main() {
	port := ":8081"
	http.HandleFunc("/temperature", handleTemperature)
	http.HandleFunc("/temperature/", handleTemperature)
	log.Printf("Temperature API starting on %s", port)
	if err := http.ListenAndServe(port, nil); err != nil {
		log.Fatalf("Failed to start server: %v", err)
	}
}
