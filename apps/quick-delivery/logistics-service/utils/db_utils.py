from db.models import db

def init_db():
    db.create_all()
