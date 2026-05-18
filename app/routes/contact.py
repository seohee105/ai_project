from fastapi import APIRouter
import hashlib

router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "contact router 정상 작동"}

@router.post("/match")
async def match_contact(
    user_id: str,
    phone: str
):
    # 전화번호 해시 처리 (개인정보 보호)
    normalized = phone.replace("-", "").replace(" ", "")
    phone_hash = hashlib.sha256(normalized.encode()).hexdigest()

    return {
        "user_id": user_id,
        "phone_hash": phone_hash,
        "message": "연락처 해시 처리 완료"
    }

@router.post("/notify")
async def notify_trade(
    buyer_id: str,
    seller_id: str,
    trade_id: str
):
    # 실제 서비스에서는 카카오/SMS API 연동
    # 지금은 알림 메시지 구조만 반환
    message = f"[안심거래 알림] 거래 상대방이 근처에 있습니다. 거래 ID: {trade_id}"

    return {
        "buyer_id": buyer_id,
        "seller_id": seller_id,
        "trade_id": trade_id,
        "message": message,
        "status": "알림 발송 완료"
    }