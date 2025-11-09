from datetime import datetime
from typing import Optional

from pydantic import BaseModel


class Device(BaseModel):
    id: int
    houseId: int
    name: str
    type: str
    location: str
    macAddress: str
    status: str
    createdAt: datetime
    updatedAt: datetime


class DeviceValue(BaseModel):
    value: float


class DeviceEvent(BaseModel):
    eventType: str
    device: Device
    deviceValue: Optional[DeviceValue] = None


class DeviceState(BaseModel):
    name: str
    device_type: str
    mac_address: str
    active: bool
    value: Optional[float]
