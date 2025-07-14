package one.globa.api.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import one.globa.api.adapter.out.DeviceMapper;
import one.globa.api.adapter.out.entity.JpaDeviceEntity;
import one.globa.api.application.port.out.DeviceRepository;
import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;
import one.globa.api.presentation.dto.PaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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
        String stateName = state != null ? state.name() : null;

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
