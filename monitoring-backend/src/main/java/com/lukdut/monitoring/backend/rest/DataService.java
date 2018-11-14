package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.model.SensorMessage;
import com.lukdut.monitoring.backend.repository.DataRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/data")
public class DataService {
    private final DataRepository dataRepository;

    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @GetMapping("/lastData")
    @ApiOperation(value = "Read last data for every specified device",
            notes = "Will get all data for every device with the given imei")
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
            notes = "Will get all data for device with the given imei between two unix-time timestamps (in milliseconds)")
    public List<String> getLogData(Long imei, Long beginTimestamp, Long endTimestamp) {
        List<String> result;
        if (imei == null || imei == 0) {
            result = new ArrayList<>();
        } else {
            Date endDate;
            if (endTimestamp == null || endTimestamp == 0) {
                endDate = new Date();
            } else {
                endDate = new Date(endTimestamp);
            }

            if (beginTimestamp >= endDate.getTime()) {
                result = new ArrayList<>();
            } else {
                result = dataRepository.findDataLog(imei, new Date(beginTimestamp), endDate).stream()
                        .map(SensorMessage::getData).collect(Collectors.toList());
            }
        }
        return result;
    }
}
