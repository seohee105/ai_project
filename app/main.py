from fastapi import FastAPI
from app.routes import location, trade, contact
from app.database import engine, Base

# 모델 import (테이블 자동 생성을 위해 필요)
from app.models import user
from app.models import location as loc_model
from app.models import trade as trade_model

app = FastAPI(title="BE3 - System & Integration API")

@app.on_event("startup")
async def startup():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

app.include_router(location.router, prefix="/api/location", tags=["Location"])
app.include_router(trade.router,    prefix="/api/trade",    tags=["Trade"])
app.include_router(contact.router,  prefix="/api/contact",  tags=["Contact"])

@app.get("/")
async def root():
    return {"message": "BE3 서버 정상 작동 중"}