package one.global.api.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import one.global.api.adapter.out.DeviceMapper;
import one.global.api.adapter.out.entity.JpaDeviceEntity;
import one.global.api.application.port.out.DeviceRepository;
import one.global.api.domain.Utils.Utils;
import one.global.api.domain.enums.State;
import one.global.api.domain.model.Device;
import one.global.api.presentation.dto.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceRespositoryAdapter implements DeviceRepository {

    private final JpaDeviceRepository jpaDeviceRepository;
    private final DeviceMapper deviceMapper;

    @Override
    public Device save(Device device) {
        JpaDeviceEntity entity = deviceMapper.fromDeviceToJpaDeviceEntity(device);
        JpaDeviceEntity savedEntity = jpaDeviceRepository.save(entity);
        return deviceMapper.fromJpaDeviceEntityToDevice(savedEntity);
    }

    @Override
    public Device findById(Long id) {
        var optionalEntity = jpaDeviceRepository.findById(id);
        return optionalEntity.map(deviceMapper::fromJpaDeviceEntityToDevice).orElse(null);
    }

    @Override
    public PaginatedResponse<Device> findAll(String brand, State state, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String stateName = Utils.getNameFromState(state);

        Page<JpaDeviceEntity> jpaPage = jpaDeviceRepository.findAllByBrandOrState(brand, stateName, pageable);

        List<Device> devices = jpaPage.getContent().stream()
                .map(deviceMapper::fromJpaDeviceEntityToDevice).toList();

        return  new PaginatedResponse<>(devices, jpaPage.getNumber(), jpaPage.getSize(),
                jpaPage.getTotalElements(), jpaPage.getTotalPages());
    }

    @Override
    public void update(Long id, Device device) {


    }
    @Override
    public void delete(Long id) {

    }

}
