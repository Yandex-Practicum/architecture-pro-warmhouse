import random
from datetime import datetime, timezone
from typing import Optional

from fastapi import APIRouter
from pydantic import BaseModel


class TemperatureResponse(BaseModel):
    value: float
    unit: str = "°C"
    timestamp: datetime = datetime.now(timezone.utc)
    location: str
    status: str = "active"
    sensor_id: str
    sensor_type: str = "temperature"
    description: str = "Some temperature sensor"


router = APIRouter(prefix="/temperature")


def get_temperature_internal(sensor_id: Optional[str] = None, location: Optional[str] = None):
    if not location and not sensor_id:
        sensor_id = str(random.randint(0, 3))

    if not location:
        if sensor_id == "1":
            location = "Living Room"
        elif sensor_id == "2":
            location = "Bedroom"
        elif sensor_id == "3":
            location = "Kitchen"
        else:
            location = "Unknown"

    if not sensor_id:
        if location == "Living Room":
            sensor_id = "1"
        elif location == "Bedroom":
            sensor_id = "2"
        elif location == "Kitchen":
            sensor_id = "3"
        else:
            sensor_id = "0"

    value = random.randint(-50, 40)

    return TemperatureResponse(
        value=value,
        sensor_id=sensor_id,
        location=location,
    )


@router.get("", response_model=TemperatureResponse)
async def get_temperature(sensor_id: Optional[str] = None, location: Optional[str] = None):
    return get_temperature_internal(sensor_id=sensor_id, location=location)


@router.get("/{sensor_id}", response_model=TemperatureResponse)
async def get_sensor_temperature(sensor_id: str):
    return get_temperature_internal(sensor_id=sensor_id)
