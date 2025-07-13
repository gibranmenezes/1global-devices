package one.globa.api.adapter.out;

import one.globa.api.adapter.out.entity.JpaDeviceEntity;
import one.globa.api.domain.model.Device;
import one.globa.api.presentation.dto.DeviceResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "brand", target = "brand")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "creationDate", target = "creationDate")
    JpaDeviceEntity fromDeviceToJpaDeviceEntity(Device device);

    Device fromJpaDeviceEntityToDevice(JpaDeviceEntity jpaDeviceEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDomain(Device device, @MappingTarget JpaDeviceEntity entity);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "brand", target = "brand")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "creationDate", target = "creationDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    DeviceResponseDTO fromDeviceToDeviceResponseDTO(Device device);



}
