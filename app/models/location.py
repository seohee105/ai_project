from sqlalchemy import Column, String, Float, DateTime, ForeignKey
from sqlalchemy.sql import func
from app.database import Base

class UserLocation(Base):
    __tablename__ = "user_locations"

    id         = Column(String, primary_key=True)
    user_id    = Column(String, ForeignKey("users.id"), nullable=False)
    trade_id   = Column(String, ForeignKey("trades.id"), nullable=True)
    latitude   = Column(Float, nullable=False)
    longitude  = Column(Float, nullable=False)
    updated_at = Column(DateTime, server_default=func.now(), onupdate=func.now())

class SafeZone(Base):
    __tablename__ = "safe_zones"

    id       = Column(String, primary_key=True)
    name     = Column(String, nullable=False)   # 예: "GS25 강남점"
    latitude = Column(Float, nullable=False)
    longitude= Column(Float, nullable=False)
    radius   = Column(Float, default=50.0)      # 반경 (미터)