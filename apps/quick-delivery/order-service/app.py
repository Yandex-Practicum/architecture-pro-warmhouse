from flask import Flask, request, jsonify
from db.models import db, Order
from utils.db_utils import init_db

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://postgres:password@db:5432/orders'
db.init_app(app)

@app.route('/orders', methods=['POST'])
def create_order():
    data = request.get_json()
    new_order = Order(customer_name=data['customer_name'], item=data['item'], status='Pending')
    db.session.add(new_order)
    db.session.commit()
    return jsonify({'message': 'Order created successfully'}), 201

@app.route('/orders/<int:order_id>', methods=['PUT'])
def update_order(order_id):
    data = request.get_json()
    order = Order.query.get(order_id)
    if not order:
        return jsonify({'message': 'Order not found'}), 404
    order.status = data['status']
    db.session.commit()
    return jsonify({'message': 'Order updated successfully'})

if __name__ == '__main__':
    with app.app_context():
        init_db()
    app.run(host='0.0.0.0', port=5000)
