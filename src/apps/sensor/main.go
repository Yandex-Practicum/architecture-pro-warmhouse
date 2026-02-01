package main

import (
	"fmt"
	"log"
	"math/rand/v2"
	"net/http"
	"os"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
)

func main() {
	router := gin.Default()
	router.GET("/temperature/:id", func(c *gin.Context) {
		_, err := strconv.Atoi(c.Param("id"))
		idStr := c.Param("id")
		if (err != nil) {
			idStr = "default"
		}

		response := TemperatureResponse {
			Value: float64(rand.IntN(101) - 50),
			Unit: "celsius",
			Timestamp: time.Now(),
			Location: "home",
			Status: "Ok",
			SensorID: idStr,
			SensorType: "temperature",
			Description: "fake data",
		}

		c.JSON(http.StatusOK, response)
	})

	router.GET("/temperature", func(c *gin.Context) {
		location := c.Query("location")

		if (len(location) == 0){
			location = "default"
		}

		response := TemperatureResponse {
			Value: float64(rand.IntN(101) - 50),
			Unit: "celsius",
			Timestamp: time.Now(),
			Location: location,
			Status: "Ok",
			SensorID: fmt.Sprint(rand.IntN(1000)),
			SensorType: "temperature",
			Description: "fake data",
		}
		
		c.JSON(http.StatusOK, response)
	})

	port := os.Getenv("PORT")
	if (port == ""){
		port = "8000"
	}


	// Start server
	srv := &http.Server{
		Addr: fmt.Sprintf("localhost:%s", port),
		Handler: router,
	}

	if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
		log.Fatalf("Failed to start server: %v\n", err)
	}
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
