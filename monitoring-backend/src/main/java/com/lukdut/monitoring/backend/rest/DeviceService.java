package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.model.Sensor;
import com.lukdut.monitoring.backend.repository.SensorRepository;
import com.lukdut.monitoring.backend.rest.resources.DeviceDto;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/device")
public class DeviceService {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    private final SensorRepository sensorRepository;

    public DeviceService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @PostMapping("/add")
    @ApiOperation(value = "Register new device",
            notes = "Will register new device with the specified imei")
    public synchronized long add(@RequestBody DeviceDto deviceDto) {
        if (deviceDto.getImei() == null || deviceDto.getImei() == 0) {
            return 0;
        }
        Long imei = deviceDto.getImei();
        if (!sensorRepository.existsByImei(imei)) {
            try {
                sensorRepository.save(new Sensor(imei));
                LOG.info("New device registered with imei {}", imei);
            } catch (Exception e) {
                LOG.warn("Can not register new device with imei={}", imei, e);
            }
        }
        return imei;
    }

    @GetMapping("/knownImei")
    @ApiOperation(value = "Read all registered imeis")
    public Collection<Long> all() {
        LOG.debug("getting all devices' imeis");
        return StreamSupport.stream(sensorRepository.findAll().spliterator(), false)
                .map(Sensor::getImei)
                .collect(Collectors.toList());
    }

    @PutMapping("/update")
    @ApiOperation(value = "Update device",
            notes = "Will update device info with the specified imei")
    public synchronized void update(@RequestBody DeviceDto deviceDto) {
        LOG.debug("Updating device " + deviceDto);
        if (deviceDto.getImei() != null && deviceDto.getImei() != 0) {
            Optional<Sensor> byImei = sensorRepository.findByImei(deviceDto.getImei());

            byImei.ifPresent(sensor -> {
                if (deviceDto.getDescription() != null) {
                    sensor.setDescription(deviceDto.getDescription());
                }
                if (deviceDto.getName() != null) {
                    sensor.setName(deviceDto.getName());
                }
                sensorRepository.save(sensor);
            });
        }
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "!!!Delete device with the specified imei!!!")
    public void del(@RequestParam Long imei) {
        if (imei != null && imei != 0) {
            LOG.warn("deleting device with imei={}" + imei);
            sensorRepository.deleteByImei(imei);
        }
    }

    @GetMapping("/status")
    @ApiOperation(value = "Get device status",
            notes = "Will return status of the command device specified imei")
    public String status(@RequestParam Long imei) {
        if (imei == null || imei == 0) {
            return "ABSENT";
        }
        Optional<Sensor> optionalSensor = sensorRepository.findById(imei);
        return optionalSensor.map(Sensor::getState).orElse("ABSENT");
    }
}
