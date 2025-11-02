from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class Order(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    customer_name = db.Column(db.String(80), nullable=False)
    item = db.Column(db.String(120), nullable=False)
    status = db.Column(db.String(20), nullable=False)
