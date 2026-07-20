#!/bin/bash

# Exit on any error
set -e

echo "Starting the Smart Home Sensor API with microservices..."
echo "Building and starting containers..."
docker-compose up --build -d

echo "Waiting for services to be ready..."

# Wait for PostgreSQL to be ready
for i in {1..30}; do
  if docker exec smarthome-postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo "PostgreSQL is ready!"
    break
  fi
  echo "Waiting for PostgreSQL to start... ($i/30)"
  sleep 1
done

# Check if PostgreSQL is ready
if ! docker exec smarthome-postgres pg_isready -U postgres > /dev/null 2>&1; then
  echo "Error: PostgreSQL did not start within the expected time."
  exit 1
fi

# Wait for microservices health checks
echo "Waiting for microservices to start..."
sleep 5

for service in temperature-api device-service telemetry-service; do
  for i in {1..20}; do
    if docker inspect --format='{{.State.Health.Status}}' $service > /dev/null 2>&1 || \
       docker inspect --format='{{.State.Running}}' $service | grep -q true; then
      echo "$service is running!"
      break
    fi
    echo "Waiting for $service... ($i/20)"
    sleep 1
  done
done

# Wait for main app to be ready
for i in {1..20}; do
  if curl -s http://localhost:8080/health > /dev/null 2>&1; then
    echo "Smart Home API is ready!"
    break
  fi
  echo "Waiting for Smart Home API... ($i/20)"
  sleep 1
done

echo ""
echo "All services are up and running!"
echo "  - Smart Home API (monolith):      http://localhost:8080"
echo "  - Temperature API:                http://localhost:8081"
echo "  - Device Service (Python/Flask):  http://localhost:8082"
echo "  - Telemetry Service (Java):       http://localhost:8083"
echo ""
echo "To view logs, run: docker-compose logs -f"
echo "To stop the services, run: docker-compose down"
