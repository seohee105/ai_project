from sqlalchemy import Column, String, DateTime
from sqlalchemy.sql import func
from app.database import Base

class User(Base):
    __tablename__ = "users"

    id          = Column(String, primary_key=True)
    phone_hash  = Column(String, unique=True, nullable=False)  # 해시된 전화번호
    kakao_token = Column(String, nullable=True)
    created_at  = Column(DateTime, server_default=func.now())