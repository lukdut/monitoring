package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.model.Command;
import com.lukdut.monitoring.backend.repository.CommandRepository;
import com.lukdut.monitoring.backend.repository.SensorRepository;
import com.lukdut.monitoring.backend.rest.resources.CommandDto;
import com.lukdut.monitoring.gateway.dto.CommandState;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/command")
public class CommandService {
    private final SensorRepository sensorRepository;
    private final CommandRepository commandRepository;

    public CommandService(SensorRepository sensorRepository, CommandRepository commandRepository) {
        this.sensorRepository = sensorRepository;
        this.commandRepository = commandRepository;
    }

    @PostMapping("/add")
    public Long add(@RequestBody CommandDto commandDto) {
        if (commandDto.getImei() != 0 && commandDto.getCommand() != null) {
            if (sensorRepository.existsByImei(commandDto.getImei())) {
                return commandRepository.save(new Command(commandDto.getImei(), commandDto.getCommand())).getId();
            }
        }
        return 0L;
    }

    @GetMapping("/status")
    public CommandState status(@RequestParam Long commandId) {
        if (commandId == null) {
            return CommandState.NOT_FOUND;
        }
        Optional<Command> byId = commandRepository.findById(commandId);
        if (byId.isPresent()) {
            return byId.get().getState();
        } else {
            return CommandState.NOT_FOUND;
        }
    }
}
