package org.smarthome.deviceservice.device;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.smarthome.common.events.DeviceValue;
import org.smarthome.common.model.DeviceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<Device>> list(
            @RequestParam(required = false) Long houseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location
    ) {
        List<Device> devices;
        try {
            DeviceType deviceType = ObjectUtils.isEmpty(type) ? null : DeviceType.valueOf(type.toUpperCase());
            devices = deviceService.list(houseId, deviceType, location);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getById(
            @PathVariable Long id
    ) {
        Optional<Device> device = deviceService.getById(id);
        return device.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/macAddress/{macAddress}")
    public ResponseEntity<Device> getByMacAddress(
            @PathVariable String macAddress
    ) {
        Optional<Device> device = deviceService.getByMacAddress(macAddress);
        return device.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Device> create(
            @Valid @RequestBody Device.CreateRequest deviceCreateRequest
    ) {
        Device createdDevice = deviceService.create(deviceCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> update(
            @PathVariable Long id,
            @Valid @RequestBody Device.UpdateRequest updateRequest
    ) {
        Optional<Device> updatedDevice = deviceService.update(id, updateRequest);
        return updatedDevice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteDevice(
            @PathVariable Long id
    ) {
        return deviceService.delete(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/activate/{id}")
    public ResponseEntity<Device> activate(
            @PathVariable Long id
    ) {
        Optional<Device> updatedDevice = deviceService.activate(id);
        return updatedDevice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<Device> deactivate(
            @PathVariable Long id
    ) {
        Optional<Device> updatedDevice = deviceService.deactivate(id);
        return updatedDevice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/setValue/{id}")
    public ResponseEntity<DeviceValue> setDeviceValue(
            @PathVariable Long id,
            @RequestBody @NotNull @Valid DeviceValue deviceValue
    ) {
        Optional<DeviceValue> updatedDevice = deviceService.setValue(id, deviceValue);
        return updatedDevice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
