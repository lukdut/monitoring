package com.lukdut.monitoring.backend.repository;

import com.lukdut.monitoring.backend.model.Sensor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface SensorRepository extends PagingAndSortingRepository<Sensor, Long> {

    boolean existsByImei(@Param("imei") Long imei);

    Optional<Sensor> findByImei(@Param("imei") Long imei);

    @Transactional
    void deleteByImei(@Param("imei") Long imei);
}
