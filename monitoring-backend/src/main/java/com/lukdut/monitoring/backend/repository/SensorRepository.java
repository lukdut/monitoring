package com.lukdut.monitoring.backend.repository;

import com.lukdut.monitoring.backend.model.Sensor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "sensor")
public interface SensorRepository extends PagingAndSortingRepository<Sensor, Long> {

    @RestResource(path = "existsByImei", rel = "existsByImei")
    boolean existsByImei(@Param("imei") Long imei);
}
