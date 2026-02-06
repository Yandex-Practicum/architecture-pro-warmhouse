from sqlalchemy import Column, String, Float, DateTime
from sqlalchemy.sql import func
from .db import Base
import uuid

def uuid_str() -> str:
    return str(uuid.uuid4())

class Device(Base):
    __tablename__ = "devices"
    __table_args__ = {"schema": "devices"}

    id = Column(String, primary_key=True, default=uuid_str)
    name = Column(String, nullable=False)
    location = Column(String, nullable=False)
    sensor_id = Column(String, nullable=False)
    last_temperature = Column(Float, nullable=True)
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())
    created_at = Column(DateTime(timezone=True), server_default=func.now())
