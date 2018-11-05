package com.lukdut.monitoring.backend.repository;

import com.lukdut.monitoring.backend.model.SensorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "data")
public interface DataRepository extends PagingAndSortingRepository<SensorMessage, Long> {
    @RestResource(path = "byImei", rel = "byImei")
    Page<SensorMessage> findAllByImeiOrderByTimestampDesc(Pageable pageable, @Param("imei") Long imei);

    @Query("select distinct message.imei from SensorMessage message")
    List<Long> findAllDistinctImei();

    /*
    В идеале хорошо бы заставить работать вот это:
    @RestResource(path = "allImei", rel = "allImei")
    @Query("select distinct message.imei from SensorMessage message")
    List<Long> findAllDistinctImei(Pageable pageable);

    java.lang.IllegalArgumentException: Couldn't find PersistentEntity for type class java.lang.Long!
    */
}
