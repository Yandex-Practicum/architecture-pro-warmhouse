from flask import Flask, request, jsonify
from db.models import db, Delivery
from utils.db_utils import init_db

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://postgres:password@db:5432/logistics'
db.init_app(app)

@app.route('/deliveries', methods=['POST'])
def create_delivery():
    data = request.get_json()
    new_delivery = Delivery(order_id=data['order_id'], status='In Transit')
    db.session.add(new_delivery)
    db.session.commit()
    return jsonify({'message': 'Delivery created successfully'}), 201

@app.route('/deliveries/<int:delivery_id>', methods=['PUT'])
def update_delivery(delivery_id):
    data = request.get_json()
    delivery = Delivery.query.get(delivery_id)
    if not delivery:
        return jsonify({'message': 'Delivery not found'}), 404
    delivery.status = data['status']
    db.session.commit()
    return jsonify({'message': 'Delivery updated successfully'})

if __name__ == '__main__':
    with app.app_context():
        init_db()
    app.run(host='0.0.0.0', port=5001)
