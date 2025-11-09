import uvicorn
from fastapi import FastAPI

from health_router import router as healthcheck_router
from temperature_router import router as temperature_router

app = FastAPI()

app.include_router(healthcheck_router)
app.include_router(temperature_router)

if __name__ == '__main__':
    uvicorn.run("app:app", host="0.0.0.0", port=8091, reload=True)
