package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.model.SensorMessage;
import com.lukdut.monitoring.backend.repository.DataRepository;
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
    public Map<Long, String> getLastData(Long[] imeis) {
        if (imeis == null || imeis.length == 0) {
            return new HashMap<>();
        } else {
            return dataRepository.findLastData(Arrays.asList(imeis)).stream()
                    .collect(Collectors.toMap(SensorMessage::getImei, SensorMessage::getData));
        }
    }

    //TODO
    @GetMapping("/log")
    public List<String> getLastData(Long imei, Long beginDate) {
        return new ArrayList<>();
    }
}
