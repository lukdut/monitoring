package com.lukdut.monitoring.backend.repository;

import com.lukdut.monitoring.backend.model.SensorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "data")
public interface DataRepository extends PagingAndSortingRepository<SensorMessage, Long> {
    @RestResource(path = "byImei", rel = "byImei")
    Page<SensorMessage> findAllByImeiOrderByTimestampDesc(Pageable pageable, @Param("imei") Long imei);
}
