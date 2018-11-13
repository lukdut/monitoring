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

    @Query(value = "SELECT a.*\n" +
            "FROM SENSOR_MESSAGE a\n" +
            "       INNER JOIN (SELECT IMEI, MAX(TIMESTAMP) TIMESTAMP\n" +
            "                   FROM SENSOR_MESSAGE\n" +
            "                   where IMEI in (:imeiList)\n" +
            "                     and DATA is not null\n" +
            "                   GROUP BY IMEI) b ON a.IMEI = b.IMEI and a.TIMESTAMP = b.TIMESTAMP where DATA is not null;",
            nativeQuery = true)
    List<SensorMessage> findLastData(@Param("imeiList") List<Long> imeiList);
}
