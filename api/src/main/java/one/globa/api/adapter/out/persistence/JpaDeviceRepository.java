package one.globa.api.adapter.out.persistence;

import one.globa.api.adapter.out.entity.JpaDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaDeviceRepository extends JpaRepository<JpaDeviceEntity, Long> {

    JpaDeviceEntity findById(long id);

    @Query("SELECT d FROM JpaDeviceEntity d WHERE (:brand IS NULL OR d.brand = :brand) AND (:state IS NULL OR d.state = :state)")
    List<JpaDeviceEntity> findAllByBrandOrState(@Param("brand") String brand, @Param("state") String state);

}



