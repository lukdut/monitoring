package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.repository.DataRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataService {
    private final DataRepository dataRepository;

    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @GetMapping("/lastData")
    //FIXME
    public Map<Long, String> getLastData(List<Long> imeis) {
        return new HashMap<>();
        /*if (imeis == null || imeis.isEmpty()) {
            return new HashMap<>();
        } else {
            return dataRepository.findAllFirst1ByImeiOrderByTimestampDesc(imeis).stream()
                    .collect(Collectors.toMap(SensorMessage::getImei, SensorMessage::getData));
        }*/
    }

    //TODO
    @GetMapping("/log")
    public List<String> getLastData(Long imei, Long beginDate) {
        return new ArrayList<>();
    }
}
