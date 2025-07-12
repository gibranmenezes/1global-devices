package one.globa.api.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import one.globa.api.adapter.out.DeviceMapper;
import one.globa.api.adapter.out.entity.JpaDeviceEntity;
import one.globa.api.application.port.out.DeviceRepository;
import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DeviceRespositoryAdapter implements DeviceRepository {

    private final JpaDeviceRepository jpaDeviceRepository;

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public Device save(Device device) {
        JpaDeviceEntity entity = deviceMapper.fromDeviceToJpaDeviceEntity(device);
        JpaDeviceEntity savedEntity = jpaDeviceRepository.save(entity);
        return deviceMapper.fromJpaDeviceEntityToDevice(savedEntity);
    }

    @Override
    public Device findById(Long id) {
        var optionalEntity = jpaDeviceRepository.findById(id);
        return optionalEntity.map(jpaDeviceEntity ->
                deviceMapper.fromJpaDeviceEntityToDevice(jpaDeviceEntity)).orElse(null);
    }

    @Override
    public List<Device> findAll(String brand, State state) {
        return  null;
    }

    @Override
    public void update(Long id, Device device) {


    }
    @Override
    public void delete(Long id) {

    }
}
