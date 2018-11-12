package com.lukdut.monitoring.backend.repository;

import com.lukdut.monitoring.backend.model.SensorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DataRepository extends PagingAndSortingRepository<SensorMessage, Long> {
    Page<SensorMessage> findAllByImeiOrderByTimestampDesc(Pageable pageable, @Param("imei") Long imei);

    @Deprecated
    @Query("select distinct message.imei from SensorMessage message")
    List<Long> findAllDistinctImei();

    List<SensorMessage> findAllFirst1ByImeiOrderByTimestampDesc(List<Long> imeis);
}
