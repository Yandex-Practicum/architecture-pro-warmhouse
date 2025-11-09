package org.smarthome.deviceservice.device;

import jakarta.validation.constraints.NotNull;
import org.smarthome.common.model.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByMacAddress(
            String macAddress
    );

    List<Device> findByHouseId(
            Long houseId
    );

    List<Device> findByType(
            DeviceType type
    );

    List<Device> findByLocationContainingIgnoreCase(
            String location
    );

    @Query("""
            SELECT d
            FROM Device d
            WHERE (:houseId IS NULL OR d.houseId = :houseId)
                AND (:type IS NULL OR d.type = :type)
                AND (:location IS NULL OR LOWER(d.location) LIKE LOWER(CONCAT('%', :location, '%')))
            """)
    List<Device> listWithParams(
            @Nullable Long houseId,
            @Nullable DeviceType type,
            @NotNull String location
    );
}
