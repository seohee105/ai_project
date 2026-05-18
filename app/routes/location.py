from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import text
from app.database import get_db
from app.services.gps_smoother import GPSSmoother
import uuid

router = APIRouter()

# 유저별 smoother 인스턴스 저장
smoother_cache = {}

@router.get("/health")
async def health_check():
    return {"status": "location router 정상 작동"}

@router.post("/update")
async def update_location(
    user_id: str,
    lat: float,
    lng: float,
    trade_id: str = None,
    db: AsyncSession = Depends(get_db)
):
    # 1. 유저별 smoother 생성
    if user_id not in smoother_cache:
        smoother_cache[user_id] = GPSSmoother()

    # 2. GPS 스무딩
    smoothed_lat, smoothed_lng = smoother_cache[user_id].smooth(lat, lng)

    # 3. DB에 저장 (있으면 업데이트, 없으면 삽입)
    existing = await db.execute(
        text("SELECT id FROM user_locations WHERE user_id = :user_id"),
        {"user_id": user_id}
    )
    row = existing.fetchone()

    if row:
        await db.execute(
            text("""
                UPDATE user_locations 
                SET latitude = :lat, longitude = :lng, updated_at = now()
                WHERE user_id = :user_id
            """),
            {"lat": smoothed_lat, "lng": smoothed_lng, "user_id": user_id}
        )
    else:
        await db.execute(
            text("""
                INSERT INTO user_locations (id, user_id, trade_id, latitude, longitude)
                VALUES (:id, :user_id, :trade_id, :lat, :lng)
            """),
            {
                "id": str(uuid.uuid4()),
                "user_id": user_id,
                "trade_id": trade_id,
                "lat": smoothed_lat,
                "lng": smoothed_lng
            }
        )

    await db.commit()

    return {
        "user_id": user_id,
        "original": {"lat": lat, "lng": lng},
        "smoothed": {"lat": smoothed_lat, "lng": smoothed_lng}
    }
@router.post("/safe-zone/verify")
async def verify_safe_zone(
    user_lat: float,
    user_lng: float,
    db: AsyncSession = Depends(get_db)
):
    # DB에서 안심구역 목록 조회
    result = await db.execute(text("SELECT * FROM safe_zones"))
    zones = result.fetchall()

    for zone in zones:
        # 거리 계산
        R = 6371000
        import math
        phi1 = math.radians(user_lat)
        phi2 = math.radians(zone.latitude)
        dphi = math.radians(zone.latitude - user_lat)
        dlambda = math.radians(zone.longitude - user_lng)
        a = math.sin(dphi/2)**2 + math.cos(phi1) * math.cos(phi2) * math.sin(dlambda/2)**2
        distance = R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

        if distance <= zone.radius:
            return {
                "is_safe": True,
                "zone_name": zone.name,
                "distance_meters": round(distance, 1)
            }

    return {"is_safe": False, "zone_name": None, "distance_meters": None}