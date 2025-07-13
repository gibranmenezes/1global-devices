package one.globa.api.adapter.out;

import one.globa.api.adapter.out.entity.JpaDeviceEntity;
import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;
import one.globa.api.presentation.dto.DeviceResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    JpaDeviceEntity fromDeviceToJpaDeviceEntity(Device device);

    Device fromJpaDeviceEntityToDevice(JpaDeviceEntity jpaDeviceEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDomain(Device device, @MappingTarget JpaDeviceEntity entity);

    DeviceResponseDTO fromDeviceToDeviceResponseDTO(Device device);



}
