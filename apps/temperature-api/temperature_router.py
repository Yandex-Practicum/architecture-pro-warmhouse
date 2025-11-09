import random
from datetime import datetime, timezone

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


@router.get("", response_model=TemperatureResponse)
async def get_location_temperature(location: str = "some_location"):
    value = random.randint(-50, 40)
    return TemperatureResponse(
        value=value,
        sensor_id="some_sensor_id",
        location=location,
    )


@router.get("/{sensor_id}", response_model=TemperatureResponse)
async def get_sensor_temperature(sensor_id: str):
    value = random.randint(-50, 40)
    return TemperatureResponse(
        value=value,
        sensor_id=sensor_id,
        location="some_location",
    )
