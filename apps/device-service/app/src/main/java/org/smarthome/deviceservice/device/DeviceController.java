package org.smarthome.deviceservice.device;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Device Management", description = "API for management the Smart Devices")
@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Operation(summary = "Get all devices", description = "Retrieve all registered devices")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved devices")
    @GetMapping
    public ResponseEntity<List<Device>> list(
            @Parameter(description = "Filter by user ID")
            @RequestParam(required = false) Long houseId,
            @Parameter(description = "Filter by device type")
            @RequestParam(required = false) String type,
            @Parameter(description = "Filter by location")
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

    @Operation(summary = "Get device by ID", description = "Retrieve a specific device by its ID")
    @ApiResponse(responseCode = "200", description = "Device found")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @GetMapping("/{id}")
    public ResponseEntity<Device> getById(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id
    ) {
        Optional<Device> device = deviceService.getById(id);
        return device.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get device by MacAddress", description = "Retrieve a specific device by its MacAddress")
    @ApiResponse(responseCode = "200", description = "Device with specified MacAddress")
    @ApiResponse(responseCode = "404", description = "Device with specified MacAddress not found")
    @GetMapping("/macAddress/{macAddress}")
    public ResponseEntity<Device> getByMacAddress(
            @PathVariable String macAddress
    ) {
        Optional<Device> device = deviceService.getByMacAddress(macAddress);
        return device.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new device", description = "Register a new device in the system")
    @ApiResponse(responseCode = "201", description = "Device created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid device data")
    @PostMapping
    public ResponseEntity<Device> create(
            @Parameter(description = "Device data", required = true)
            @Valid @RequestBody Device.CreateRequest deviceCreateRequest
    ) {
        Device createdDevice = deviceService.create(deviceCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
    }

    @Operation(summary = "Update device", description = "Update an existing device")
    @ApiResponse(responseCode = "200", description = "Device updated successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @ApiResponse(responseCode = "400", description = "Invalid device data")
    @PutMapping("/{id}")
    public ResponseEntity<Device> update(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated device data", required = true)
            @Valid @RequestBody Device.UpdateRequest updateRequest
    ) {
        Optional<Device> updatedDevice = deviceService.update(id, updateRequest);
        return updatedDevice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete device", description = "Remove a device from the system. Returns id of deleted device.")
    @ApiResponse(responseCode = "200", description = "Device deleted successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteDevice(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id
    ) {
        return deviceService.delete(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Activate device", description = "Set device status to ACTIVE state. Device starts to send metrics.")
    @ApiResponse(responseCode = "200", description = "Device activated successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @PostMapping("/activate/{id}")
    public ResponseEntity<Device> activate(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id
    ) {
        Optional<Device> updatedDevice = deviceService.activate(id);
        return updatedDevice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deactivate device", description = "Set device status to INACTIVE state. Device stops to send metrics.")
    @ApiResponse(responseCode = "200", description = "Device deactivated successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @PostMapping("/deactivate/{id}")
    public ResponseEntity<Device> deactivate(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id
    ) {
        Optional<Device> updatedDevice = deviceService.deactivate(id);
        return updatedDevice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Set device value", description = "Set new value for device.")
    @ApiResponse(responseCode = "200", description = "Device deactivated successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @PostMapping("/setValue/{id}")
    public ResponseEntity<DeviceValue> setDeviceValue(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New Value Data", required = true)
            @RequestBody @NotNull @Valid DeviceValue deviceValue
    ) {
        Optional<DeviceValue> updatedDevice = deviceService.setValue(id, deviceValue);
        return updatedDevice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
