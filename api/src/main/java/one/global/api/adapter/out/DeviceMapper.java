package one.global.api.adapter.out;

import one.global.api.adapter.out.entity.JpaDeviceEntity;
import one.global.api.domain.model.Device;
import one.global.api.web.dto.DeviceResponseDTO;
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

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "brand", target = "brand")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "creationDate", target = "creationDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    DeviceResponseDTO fromDeviceToDeviceResponseDTO(Device device);

    @ObjectFactory
    default Device createDevice(JpaDeviceEntity jpaDeviceEntity, @TargetType Class<Device> targetType) {
        return Device.reconstruct(
                jpaDeviceEntity.getId(),
                jpaDeviceEntity.getName(),
                jpaDeviceEntity.getBrand(),
                jpaDeviceEntity.getState(),
                jpaDeviceEntity.getCreationDate()
        );
    }






}
