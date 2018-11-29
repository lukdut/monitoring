package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.model.Sensor;
import com.lukdut.monitoring.backend.repository.SensorRepository;
import com.lukdut.monitoring.backend.rest.dto.DeviceDto;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Sid;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/device")
public class DeviceService {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);

    private final SensorRepository sensorRepository;
    private final MutableAclService aclService;

    public DeviceService(SensorRepository sensorRepository, MutableAclService aclService) {
        this.sensorRepository = sensorRepository;
        this.aclService = aclService;
    }

    @PostMapping("/add")
    @ApiOperation(value = "Register new device",
            notes = "Will register new device with the specified imei, returns 0 if failed or already exists")
    @Transactional
    public synchronized long add(@RequestBody DeviceDto deviceDto) {
        Long imei = deviceDto.getImei();
        long id = 0;
        if (imei != null && imei != 0 & !sensorRepository.existsByImei(imei)) {
            try {
                Sensor sensor = new Sensor(imei);
                sensor.setDescription(deviceDto.getDescription());
                sensor.setName(deviceDto.getName());
                id = sensorRepository.save(sensor).getId();
                LOG.info("New device registered with imei {} and id {}", imei, id);

                final MutableAcl acl = aclService.createAcl(new ObjectIdentityImpl(Sensor.class, id));
                Sid sid = new PrincipalSid("admin");
                acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, sid, true);
                aclService.updateAcl(acl);
            } catch (Exception e) {
                LOG.warn("Can not register new device with imei={}", imei, e);
            }
        }
        return id;
    }

    @GetMapping("/all")
    @ApiOperation(value = "Read all registered devices")
    public Collection<DeviceDto> all() {
        LOG.debug("getting all devices");
        return StreamSupport.stream(sensorRepository.findAll().spliterator(), false)
                .map(sensor -> {
                    DeviceDto deviceDto = new DeviceDto();
                    deviceDto.setDescription(sensor.getDescription());
                    deviceDto.setImei(sensor.getImei());
                    deviceDto.setName(sensor.getName());
                    return deviceDto;
                })
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
    @ApiOperation(value = "!!! Delete device with the specified imei !!!")
    public synchronized void del(@RequestParam Long imei) {
        if (imei != null && imei != 0) {
            Optional<Sensor> byImei = sensorRepository.findByImei(imei);
            if (!byImei.isPresent()) {
                LOG.info("Device with imei={} does not exist", imei);
            } else {
                LOG.warn("deleting device with imei={}", imei);
                sensorRepository.deleteByImei(imei);
                aclService.deleteAcl(new ObjectIdentityImpl(Sensor.class, byImei.get().getId()), true);
            }
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
