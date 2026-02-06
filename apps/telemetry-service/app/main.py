from datetime import datetime
from fastapi import FastAPI, Query
from pydantic import BaseModel
from sqlalchemy.orm import Session

from .db import Base, engine, SessionLocal
from .models import TelemetryPoint
from .db import Base, engine, ensure_schema

ensure_schema()
Base.metadata.create_all(bind=engine)

app = FastAPI(title="telemetry-service", version="1.0.0")

class TelemetryIn(BaseModel):
    deviceId: str
    metric: str
    value: float
    timestamp: datetime | None = None

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/telemetry", status_code=201)
def write_telemetry(body: TelemetryIn):
    db = SessionLocal()
    point = TelemetryPoint(
        device_id=body.deviceId,
        metric=body.metric,
        value=body.value,
        ts=body.timestamp or datetime.utcnow(),
    )
    db.add(point)
    db.commit()
    db.refresh(point)
    db.close()
    return {
        "id": point.id,
        "deviceId": point.device_id,
        "metric": point.metric,
        "value": point.value,
        "timestamp": point.ts.isoformat()
    }

@app.get("/telemetry")
def read_telemetry(
    deviceId: str = Query(...),
    metric: str | None = Query(None),
    limit: int = Query(50, ge=1, le=500),
):
    db = SessionLocal()
    q = db.query(TelemetryPoint).filter(TelemetryPoint.device_id == deviceId)
    if metric:
        q = q.filter(TelemetryPoint.metric == metric)
    rows = q.order_by(TelemetryPoint.ts.desc()).limit(limit).all()
    db.close()
    return [
        {"id": r.id, "deviceId": r.device_id, "metric": r.metric, "value": r.value, "timestamp": r.ts.isoformat()}
        for r in rows
    ]
