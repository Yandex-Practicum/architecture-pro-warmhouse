from fastapi import APIRouter

router = APIRouter()


@router.get("/health")
async def health_check():
    return {
        "service": "temperature-api",
        "version": "1.0.0",
        "status": "running"
    }
