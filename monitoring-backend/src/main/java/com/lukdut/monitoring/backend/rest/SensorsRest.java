package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.model.Command;
import com.lukdut.monitoring.backend.model.Sensor;
import com.lukdut.monitoring.backend.repository.CommandRepository;
import com.lukdut.monitoring.backend.repository.SensorRepository;
import com.lukdut.monitoring.backend.rest.resources.CommandDto;
import com.lukdut.monitoring.backend.rest.resources.Devices;
import com.lukdut.monitoring.backend.rest.resources.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.lukdut.monitoring.backend.integration.CommandIntegrationConfig.COMMANDS_TO_KAFKA_CHANNEL;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class SensorsRest implements ResourceProcessor<RepositoryLinksResource> {
    private static final Logger LOG = LoggerFactory.getLogger(SensorsRest.class);
    private static final String ALL_DEVICES = "allDevices";
    private static final String STATUS = "status";
    private static final String REGISTER = "register";
    private static final String COMMAND = "command";

    private final SensorRepository sensorRepository;
    private final CommandRepository commandRepository;
    private final MessageChannel commandsChannel;

    public SensorsRest(
            SensorRepository sensorRepository,
            CommandRepository commandRepository,
            @Qualifier(COMMANDS_TO_KAFKA_CHANNEL) MessageChannel commandsChannel) {
        this.sensorRepository = sensorRepository;
        this.commandRepository = commandRepository;
        this.commandsChannel = commandsChannel;
    }

    @GetMapping("/" + ALL_DEVICES)
    public HttpEntity<Devices> allDevices(Pageable pageable) {
        if (pageable == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Page<Long> longs = new PageImpl<>(sensorRepository.findAll(pageable)
                .get().map(Sensor::getImei).collect(Collectors.toList()));
        Devices devices = new Devices(longs);
        devices.add(linkTo(methodOn(SensorsRest.class).allDevices(pageable)).withSelfRel());
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @GetMapping("/" + STATUS)
    public HttpEntity<Status> status(@RequestParam(value = "imei") Long imei) {
        if (imei == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Sensor> optionalSensor = sensorRepository.findById(imei);
        Status status = optionalSensor.map(sensor -> new Status(sensor.getState())).orElseGet(() -> new Status("ABSENT"));
        status.add(linkTo(methodOn(SensorsRest.class).status(imei)).withSelfRel());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/" + REGISTER)
    public HttpEntity<Long> register(@RequestBody Long imei) {
        if (imei == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!sensorRepository.existsByImei(imei)) {
            try {
                sensorRepository.save(new Sensor(imei));
                LOG.info("New device registered with imei {}", imei);
            } catch (Exception e) {
                LOG.warn("Can not register new device with imei={}", imei, e);
            }
        }

        return new ResponseEntity<>(imei, HttpStatus.OK);
    }

    @PostMapping("/" + COMMAND)
    public HttpEntity<Long> command(@RequestBody CommandDto commandDto) {
        if (commandDto == null || commandDto.getImei() == 0 || commandDto.getCommand() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (sensorRepository.existsByImei(commandDto.getImei())) {
            try {
                Command command = new Command(commandDto.getImei(), commandDto.getCommand());
                commandRepository.save(command);
                commandsChannel.send(MessageBuilder.withPayload(command).build());
                LOG.info("New command registered for imei {}", command.getImei());
                return new ResponseEntity<>(command.getId(), HttpStatus.OK);
            } catch (Exception e) {
                LOG.warn("Can not register new command with imei={}", commandDto.getImei(), e);
            }
        } else {
            LOG.warn("Device with imei {} does not exists", commandDto.getImei());
        }
        return new ResponseEntity<>(0L, HttpStatus.OK);
    }


    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(linkTo(methodOn(SensorsRest.class).allDevices(null)).withRel(ALL_DEVICES));
        resource.add(linkTo(methodOn(SensorsRest.class).status(null)).withRel(STATUS));
        resource.add(linkTo(methodOn(SensorsRest.class).register(null)).withRel(REGISTER));
        resource.add(linkTo(methodOn(SensorsRest.class).command(null)).withRel(COMMAND));
        return resource;
    }
}
