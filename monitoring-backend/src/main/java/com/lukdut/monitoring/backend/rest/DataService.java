package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.model.SensorMessage;
import com.lukdut.monitoring.backend.repository.DataRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/data")
public class DataService {
    private static final long MS_IN_DAY = 24 * 60 * 60 * 1000;
    private final DataRepository dataRepository;

    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @GetMapping("/lastData")
    @ApiOperation(value = "Read last data for every specified device",
            notes = "Will get all data for every device with the given imei")
    //TODO Filter imeis
    public Map<Long, String> getLastData(Long[] imeis) {
        if (imeis == null || imeis.length == 0) {
            return new HashMap<>();
        } else {
            return dataRepository.findLastData(Arrays.asList(imeis)).stream()
                    .collect(Collectors.toMap(SensorMessage::getImei, SensorMessage::getData));
        }
    }

    @GetMapping("/log")
    @ApiOperation(value = "Read data array for specified device",
            notes = "Will get all data for device with the given imei between two unix-time timestamps (in milliseconds)\n"
                    + "If endTimestamp is not set, endTimestamp = now\n"
                    + "If beginTimestamp is not set, beginTimestamp = endTimestamp - 1 day")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasPermission(#imei, 'READ') || hasPermission(#imei, 'WRITE')")
    public List<String> getLogData(Long imei, Long beginTimestamp, Long endTimestamp) {
        List<String> result;
        if (imei == null || imei == 0) {
            result = new ArrayList<>();
        } else {
            Date endDate;
            if (endTimestamp == null) {
                endDate = new Date();
            } else {
                endDate = new Date(endTimestamp);
            }

            Date beginDate;
            if (beginTimestamp == null) {
                beginDate = new Date(endDate.getTime() - MS_IN_DAY);
            } else {
                beginDate = new Date(beginTimestamp);
            }

            if (beginDate.getTime() >= endDate.getTime()) {
                result = new ArrayList<>();
            } else {
                result = dataRepository.findDataLog(imei, beginDate, endDate).stream()
                        .map(SensorMessage::getData).collect(Collectors.toList());
            }
        }
        return result;
    }

    @GetMapping("/aggregatedData")
    @ApiOperation(value = "Read aggregated data (Now stubbed!)",
            notes = "Will get all aggregated data for the given imei in specified time period.")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasPermission(#imei, 'READ') || hasPermission(#imei, 'WRITE')")
    public Long getAggregatedData(Long imei, Long beginTimestamp, Long endTimestamp) {
        return ThreadLocalRandom.current().nextLong(0, 1000);
    }
}
