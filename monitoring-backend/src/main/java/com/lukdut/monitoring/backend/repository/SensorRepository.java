package com.lukdut.monitoring.backend.repository;

import com.lukdut.monitoring.backend.model.Sensor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface SensorRepository extends PagingAndSortingRepository<Sensor, Long> {

    boolean existsByImei(Long imei);

    Optional<Sensor> findByImei(Long imei);

    @Transactional
    void deleteByImei(Long imei);
}
