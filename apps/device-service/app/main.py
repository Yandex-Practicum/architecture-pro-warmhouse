import os
import requests
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sqlalchemy.orm import Session

from .db import Base, engine, SessionLocal
from .models import Device
from fastapi import FastAPI
from .db import engine, Base, ensure_schema

ensure_schema()

from .models import Device

Base.metadata.create_all(bind=engine)

TEMPERATURE_API_URL = os.getenv("TEMPERATURE_API_URL", "http://temperature-api:8081")

Base.metadata.create_all(bind=engine)

app = FastAPI(title="device-service", version="1.0.0")

class DeviceCreate(BaseModel):
    name: str
    location: str = ""
    sensorId: str = ""

class DeviceUpdate(BaseModel):
    name: str | None = None
    location: str | None = None
    sensorId: str | None = None

def resolve_location_sensor(location: str, sensor_id: str) -> tuple[str, str]:
    if location == "":
        if sensor_id == "1":
            location = "Living Room"
        elif sensor_id == "2":
            location = "Bedroom"
        elif sensor_id == "3":
            location = "Kitchen"
        else:
            location = "Unknown"

    if sensor_id == "":
        if location == "Living Room":
            sensor_id = "1"
        elif location == "Bedroom":
            sensor_id = "2"
        elif location == "Kitchen":
            sensor_id = "3"
        else:
            sensor_id = "0"

    return location, sensor_id

def get_db() -> Session:
    db = SessionLocal()
    try:
        return db
    finally:
        pass

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/devices", status_code=201)
def create_device(body: DeviceCreate):
    db = SessionLocal()
    location, sensor_id = resolve_location_sensor(body.location, body.sensorId)
    dev = Device(name=body.name, location=location, sensor_id=sensor_id)
    db.add(dev)
    db.commit()
    db.refresh(dev)
    db.close()
    return dev

@app.get("/devices")
def list_devices():
    db = SessionLocal()
    items = db.query(Device).all()
    db.close()
    return items

@app.get("/devices/{device_id}")
def get_device(device_id: str):
    db = SessionLocal()
    dev = db.query(Device).filter(Device.id == device_id).first()
    db.close()
    if not dev:
        raise HTTPException(status_code=404, detail="Device not found")
    return dev

@app.patch("/devices/{device_id}")
def update_device(device_id: str, body: DeviceUpdate):
    db = SessionLocal()
    dev = db.query(Device).filter(Device.id == device_id).first()
    if not dev:
        db.close()
        raise HTTPException(status_code=404, detail="Device not found")

    if body.name is not None:
        dev.name = body.name
    if body.location is not None:
        dev.location = body.location
    if body.sensorId is not None:
        dev.sensor_id = body.sensorId

    dev.location, dev.sensor_id = resolve_location_sensor(dev.location, dev.sensor_id)

    db.commit()
    db.refresh(dev)
    db.close()
    return dev

@app.post("/devices/{device_id}/read-temperature")
def read_temperature(device_id: str):
    db = SessionLocal()
    dev = db.query(Device).filter(Device.id == device_id).first()
    if not dev:
        db.close()
        raise HTTPException(status_code=404, detail="Device not found")

    params = {"location": dev.location, "sensorId": dev.sensor_id}
    r = requests.get(f"{TEMPERATURE_API_URL}/temperature", params=params, timeout=3)
    r.raise_for_status()
    data = r.json()

    dev.last_temperature = float(data["temperature"])
    db.commit()
    db.refresh(dev)
    db.close()

    return {"deviceId": dev.id, "location": dev.location, "sensorId": dev.sensor_id, "temperature": dev.last_temperature}
