from sqlalchemy import Column, String, DateTime, Enum as SAEnum
from sqlalchemy.sql import func
from app.database import Base
import enum

class TradeStatus(str, enum.Enum):
    PENDING   = "PENDING"
    ONGOING   = "ONGOING"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"

class Trade(Base):
    __tablename__ = "trades"

    id           = Column(String, primary_key=True)
    buyer_id     = Column(String, nullable=False)
    seller_id    = Column(String, nullable=False)
    status       = Column(SAEnum(TradeStatus), default=TradeStatus.PENDING)
    created_at   = Column(DateTime, server_default=func.now())
    completed_at = Column(DateTime, nullable=True)