package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.repository.DataRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SensorsRest {

    private final DataRepository repository;

    public SensorsRest(DataRepository repository){
        this.repository = repository;
    }

    @GetMapping("/allDevices")
    public List<Long> allDevices(){
        return repository.findAllDistinctImei();
    }
}
