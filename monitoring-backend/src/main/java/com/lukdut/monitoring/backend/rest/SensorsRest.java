package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.repository.DataRepository;
import com.lukdut.monitoring.backend.rest.resources.Devices;
import com.lukdut.monitoring.backend.rest.resources.Status;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class SensorsRest implements ResourceProcessor<RepositoryLinksResource> {
    private static final String ALL_DEVICES = "allDevices";
    private static final String STATUS = "status";


    private final DataRepository repository;

    public SensorsRest(DataRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/" + ALL_DEVICES)
    public HttpEntity<Devices> allDevices() {
        Devices devices = new Devices(repository.findAllDistinctImei());
        devices.add(linkTo(methodOn(SensorsRest.class).allDevices()).withSelfRel());
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @GetMapping("/" + STATUS)
    public HttpEntity<Status> status(@RequestParam(value = "imei") Long imei) {
        //TODO get status from DB
        Status status = new Status((imei & 1L) == 0 ? "LOCKED" : "UNLOCKED");
        status.add(linkTo(methodOn(SensorsRest.class).status(imei)).withSelfRel());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(linkTo(methodOn(SensorsRest.class).allDevices()).withRel(ALL_DEVICES));
        resource.add(linkTo(methodOn(SensorsRest.class).status(null)).withRel(STATUS));
        return resource;
    }
}
