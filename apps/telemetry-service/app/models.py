from sqlalchemy import Column, String, Float, DateTime, Index
from sqlalchemy.sql import func
from .db import Base
import uuid

def uuid_str() -> str:
    return str(uuid.uuid4())

class TelemetryPoint(Base):
    __tablename__ = "telemetry_points"
    __table_args__ = (
        Index("ix_tp_device_metric_ts", "device_id", "metric", "ts"),
        {"schema": "telemetry"},
    )

    id = Column(String, primary_key=True, default=uuid_str)
    device_id = Column(String, nullable=False)
    metric = Column(String, nullable=False)
    value = Column(Float, nullable=False)
    ts = Column(DateTime(timezone=True), server_default=func.now())
