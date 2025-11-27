package handlers

import (
	"context"
	"net/http"
	"strconv"

	"device/db"
	"device/models"

	"github.com/gin-gonic/gin"
)

// DeviceHandler handles device-related requests
type DeviceHandler struct {
	DB *db.DB
}

// NewDeviceHandler creates a new DeviceHandler
func NewDeviceHandler(db *db.DB) *DeviceHandler {
	return &DeviceHandler{
		DB: db,
	}
}

// RegisterRoutes registers the device routes
func (h *DeviceHandler) RegisterRoutes(router *gin.RouterGroup) {
	devices := router.Group("/devices")
	{
		devices.GET("", h.GetDevices)
		devices.GET("/:id", h.GetDeviceByID)
		devices.POST("", h.CreateDevice)
		devices.PUT("/:id", h.UpdateDevice)
		devices.PATCH("/:id/state", h.UpdateDeviceState)
		devices.DELETE("/:id", h.DeleteDevice)
	}
}

// GetDevices handles GET /api/v1/devices
func (h *DeviceHandler) GetDevices(c *gin.Context) {
	devices, err := h.DB.GetDevices(context.Background())
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, devices)
}

// GetDeviceByID handles GET /api/v1/devices/:id
func (h *DeviceHandler) GetDeviceByID(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid device ID"})
		return
	}

	device, err := h.DB.GetDeviceByID(context.Background(), id)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Device not found"})
		return
	}

	c.JSON(http.StatusOK, device)
}

// CreateDevice handles POST /api/v1/devices
func (h *DeviceHandler) CreateDevice(c *gin.Context) {
	var deviceCreate models.DeviceCreate
	if err := c.ShouldBindJSON(&deviceCreate); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	device, err := h.DB.CreateDevice(context.Background(), deviceCreate)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, device)
}

// UpdateDevice handles PUT /api/v1/devices/:id
func (h *DeviceHandler) UpdateDevice(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid device ID"})
		return
	}

	var deviceUpdate models.DeviceUpdate
	if err := c.ShouldBindJSON(&deviceUpdate); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	device, err := h.DB.UpdateDevice(context.Background(), id, deviceUpdate)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, device)
}

// UpdateDeviceState handles PATCH /api/v1/devices/:id/state
func (h *DeviceHandler) UpdateDeviceState(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid device ID"})
		return
	}

	var stateUpdate models.DeviceStateUpdate
	if err := c.ShouldBindJSON(&stateUpdate); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	device, err := h.DB.UpdateDeviceState(context.Background(), id, stateUpdate.State)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, device)
}

// DeleteDevice handles DELETE /api/v1/devices/:id
func (h *DeviceHandler) DeleteDevice(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid device ID"})
		return
	}

	err = h.DB.DeleteDevice(context.Background(), id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Device deleted successfully"})
}


