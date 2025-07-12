package one.globa.api.adapter.out.persistence;

import one.globa.api.adapter.out.entity.JpaDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDeviceRepository extends JpaRepository<JpaDeviceEntity, Long> {


}
