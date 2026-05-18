from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from app.database import get_db
from app.routes.location import smoother_cache
import uuid

router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "trade router 정상 작동"}

@router.post("/complete")
async def complete_trade(
    trade_id: str,
    buyer_id: str,
    seller_id: str,
    db: AsyncSession = Depends(get_db)
):
    # 1. 거래 상태 COMPLETED로 업데이트
    await db.execute(
        text("""
            UPDATE trades 
            SET status = 'COMPLETED', completed_at = now()
            WHERE id = :trade_id
        """),
        {"trade_id": trade_id}
    )

    # 2. 위치 정보 즉시 삭제
    await db.execute(
        text("DELETE FROM user_locations WHERE user_id = :buyer_id"),
        {"buyer_id": buyer_id}
    )
    await db.execute(
        text("DELETE FROM user_locations WHERE user_id = :seller_id"),
        {"seller_id": seller_id}
    )

    await db.commit()

    # 3. 메모리 캐시도 삭제
    smoother_cache.pop(buyer_id, None)
    smoother_cache.pop(seller_id, None)

    return {
        "trade_id": trade_id,
        "status": "COMPLETED",
        "message": "거래 완료 및 위치 정보 삭제 완료"
    }