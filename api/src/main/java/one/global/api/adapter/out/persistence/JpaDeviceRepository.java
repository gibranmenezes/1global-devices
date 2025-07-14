package one.global.api.adapter.out.persistence;

import one.global.api.adapter.out.entity.JpaDeviceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaDeviceRepository extends JpaRepository<JpaDeviceEntity, Long> {

    JpaDeviceEntity findById(long id);

    @Query("SELECT d FROM JpaDeviceEntity d WHERE (:brand IS NULL OR d.brand = :brand) AND (:state IS NULL OR d.state = :state)")
    Page<JpaDeviceEntity> findAllByBrandOrState(@Param("brand") String brand, @Param("state") String state, Pageable pageable);

}



