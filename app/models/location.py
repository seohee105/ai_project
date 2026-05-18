from sqlalchemy import Column, String, Float, DateTime
from sqlalchemy.sql import func
from app.database import Base

class UserLocation(Base):
    __tablename__ = "user_locations"

    id         = Column(String, primary_key=True)
    user_id    = Column(String, nullable=False)  # ForeignKey 제거
    trade_id   = Column(String, nullable=True)
    latitude   = Column(Float, nullable=False)
    longitude  = Column(Float, nullable=False)
    updated_at = Column(DateTime, server_default=func.now())

class SafeZone(Base):
    __tablename__ = "safe_zones"

    id        = Column(String, primary_key=True)
    name      = Column(String, nullable=False)
    latitude  = Column(Float, nullable=False)
    longitude = Column(Float, nullable=False)
    radius    = Column(Float, default=50.0)