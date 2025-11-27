package handlers

import (
	"context"
	"net/http"
	"strconv"
	"time"

	"telemetry/db"
	"telemetry/models"

	"github.com/gin-gonic/gin"
)

// TelemetryHandler handles telemetry-related requests
type TelemetryHandler struct {
	DB *db.DB
}

// NewTelemetryHandler creates a new TelemetryHandler
func NewTelemetryHandler(db *db.DB) *TelemetryHandler {
	return &TelemetryHandler{
		DB: db,
	}
}

// RegisterRoutes registers the telemetry routes
func (h *TelemetryHandler) RegisterRoutes(router *gin.RouterGroup) {
	telemetry := router.Group("/telemetry")
	{
		telemetry.GET("/sensors/:sensorId", h.GetTelemetryBySensor)
		telemetry.GET("/sensors/:sensorId/history", h.GetTelemetryHistory)
		telemetry.POST("/collect", h.CollectTelemetry)
	}
}

// GetTelemetryBySensor handles GET /api/v1/telemetry/sensors/:sensorId
func (h *TelemetryHandler) GetTelemetryBySensor(c *gin.Context) {
	sensorIDStr := c.Param("sensorId")
	sensorID, err := strconv.Atoi(sensorIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid sensor ID"})
		return
	}

	telemetry, err := h.DB.GetTelemetryBySensor(context.Background(), sensorID)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Telemetry data not found"})
		return
	}

	c.JSON(http.StatusOK, telemetry)
}

// GetTelemetryHistory handles GET /api/v1/telemetry/sensors/:sensorId/history
func (h *TelemetryHandler) GetTelemetryHistory(c *gin.Context) {
	sensorIDStr := c.Param("sensorId")
	sensorID, err := strconv.Atoi(sensorIDStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid sensor ID"})
		return
	}

	var from, to *time.Time
	if fromStr := c.Query("from"); fromStr != "" {
		parsed, err := time.Parse(time.RFC3339, fromStr)
		if err == nil {
			from = &parsed
		}
	}

	if toStr := c.Query("to"); toStr != "" {
		parsed, err := time.Parse(time.RFC3339, toStr)
		if err == nil {
			to = &parsed
		}
	}

	limitStr := c.DefaultQuery("limit", "100")
	limit, err := strconv.Atoi(limitStr)
	if err != nil {
		limit = 100
	}

	history, err := h.DB.GetTelemetryHistory(context.Background(), sensorID, from, to, limit)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, history)
}

// CollectTelemetry handles POST /api/v1/telemetry/collect
func (h *TelemetryHandler) CollectTelemetry(c *gin.Context) {
	var request models.TelemetryCollectionRequest
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	collected, err := h.DB.CollectTelemetry(context.Background(), request.SensorIDs)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	response := models.TelemetryCollectionResponse{
		Collected: collected,
		Timestamp: time.Now(),
	}

	c.JSON(http.StatusOK, response)
}


