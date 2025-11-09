package org.smarthome.deviceservice.deviceheartbeat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
public interface DeviceHeartbeatRepository extends JpaRepository<DeviceHeartbeat, Long> {
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE devices
            SET status = :inactiveStatus
            WHERE id IN (
                SELECT id FROM devices d
                LEFT JOIN device_heartbeats h ON d.id = h.device_id
                WHERE d.status != :inactiveStatus AND (h.device_id IS NULL OR h.heartbeat_at < :heartbeatDeadline)
            )
            """,
            nativeQuery = true)
    int deactivateDevicesByFailedHeartbeats(
            @Param("inactiveStatus") String inactiveStatus,
            @Param("heartbeatDeadline") Instant heartbeatDeadline
    );
}
