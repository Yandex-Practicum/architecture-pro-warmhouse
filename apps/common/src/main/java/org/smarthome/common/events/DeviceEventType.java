package org.smarthome.common.events;

public enum DeviceEventType {
    CREATED("device.created"),
    UPDATED("device.updated"),
    DELETED("device.deleted"),
    ACTIVATED("device.activated"),
    DEACTIVATED("device.deactivated"),
    SET_VALUE("device.setValue");

    private final String value;

    DeviceEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
