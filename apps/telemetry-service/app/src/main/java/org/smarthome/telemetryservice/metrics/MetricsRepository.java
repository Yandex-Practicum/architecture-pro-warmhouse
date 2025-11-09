package org.smarthome.telemetryservice.metrics;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface MetricsRepository extends JpaRepository<DeviceMetricEntry, UUID> {
    List<DeviceMetricEntry> findByDeviceIdOrderByTimestampDesc(
            Long deviceId,
            Pageable pageable
    );

    List<DeviceMetricEntry> findByDeviceIdInOrderByTimestampDesc(
            List<Long> deviceIds,
            Pageable pageable
    );

    @Modifying
    @Transactional
    List<DeviceMetricEntry> deleteAllByDeviceId(
            Long deviceId
    );
}
