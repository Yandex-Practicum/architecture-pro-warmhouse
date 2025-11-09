package org.smarthome.common.events;

import jakarta.validation.constraints.NotNull;

public record DeviceValue(
        @NotNull
        Float value
) {
}
