from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime, timezone
import os

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get('DATABASE_URL', 'sqlite:///devices.db')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)


class Device(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    sensor_id = db.Column(db.Integer, unique=True, nullable=False, index=True)
    name = db.Column(db.String(100), nullable=False)
    device_type = db.Column(db.String(50), nullable=False)
    location = db.Column(db.String(100), nullable=False)
    unit = db.Column(db.String(20), default='°C')
    status = db.Column(db.String(20), default='active')
    created_at = db.Column(db.DateTime, default=lambda: datetime.now(timezone.utc))
    updated_at = db.Column(db.DateTime, default=lambda: datetime.now(timezone.utc),
                           onupdate=lambda: datetime.now(timezone.utc))

    def to_dict(self):
        return {
            'id': self.id,
            'sensor_id': self.sensor_id,
            'name': self.name,
            'type': self.device_type,
            'location': self.location,
            'unit': self.unit,
            'status': self.status,
            'created_at': self.created_at.isoformat() if self.created_at else None,
            'updated_at': self.updated_at.isoformat() if self.updated_at else None
        }


@app.route('/devices', methods=['GET'])
def get_devices():
    devices = Device.query.all()
    return jsonify([d.to_dict() for d in devices]), 200


@app.route('/devices/<int:device_id>', methods=['GET'])
def get_device(device_id):
    device = db.session.get(Device, device_id)
    if device is None:
        return jsonify({'error': 'Device not found'}), 404
    return jsonify(device.to_dict()), 200


@app.route('/devices/sensor/<int:sensor_id>', methods=['GET'])
def get_device_by_sensor_id(sensor_id):
    device = Device.query.filter_by(sensor_id=sensor_id).first()
    if device is None:
        return jsonify({'error': 'Device not found'}), 404
    return jsonify(device.to_dict()), 200


@app.route('/devices', methods=['POST'])
def create_device():
    data = request.get_json()
    if not data or 'sensor_id' not in data or 'name' not in data:
        return jsonify({'error': 'sensor_id and name are required'}), 400

    if Device.query.filter_by(sensor_id=data['sensor_id']).first():
        return jsonify({'error': 'Device with this sensor_id already exists'}), 409

    device = Device(
        sensor_id=data['sensor_id'],
        name=data['name'],
        device_type=data.get('type', 'temperature'),
        location=data.get('location', ''),
        unit=data.get('unit', '°C'),
        status='active'
    )
    db.session.add(device)
    db.session.commit()
    return jsonify(device.to_dict()), 201


@app.route('/devices/<int:device_id>', methods=['PUT'])
def update_device(device_id):
    device = db.session.get(Device, device_id)
    if device is None:
        return jsonify({'error': 'Device not found'}), 404

    data = request.get_json()
    if not data:
        return jsonify({'error': 'No data provided'}), 400

    if 'name' in data:
        device.name = data['name']
    if 'type' in data:
        device.device_type = data['type']
    if 'location' in data:
        device.location = data['location']
    if 'unit' in data:
        device.unit = data['unit']
    if 'status' in data:
        device.status = data['status']

    db.session.commit()
    return jsonify(device.to_dict()), 200


@app.route('/devices/<int:device_id>', methods=['DELETE'])
def delete_device(device_id):
    device = db.session.get(Device, device_id)
    if device is None:
        return jsonify({'error': 'Device not found'}), 404

    db.session.delete(device)
    db.session.commit()
    return jsonify({'message': 'Device deleted successfully'}), 200


@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok', 'service': 'device-service'}), 200


with app.app_context():
    db.create_all()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8082)
