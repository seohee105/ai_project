from fastapi import FastAPI
from app.routes import location, trade, contact
from app.database import engine, Base

app = FastAPI(title="BE3 - System & Integration API")

# 앱 시작 시 테이블 자동 생성
@app.on_event("startup")
async def startup():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

# 라우터 등록
app.include_router(location.router, prefix="/api/location", tags=["Location"])
app.include_router(trade.router,    prefix="/api/trade",    tags=["Trade"])
app.include_router(contact.router,  prefix="/api/contact",  tags=["Contact"])

@app.get("/")
async def root():
    return {"message": "BE3 서버 정상 작동 중"}