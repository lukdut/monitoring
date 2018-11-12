package com.lukdut.monitoring.backend.rest;

import com.lukdut.monitoring.backend.model.Command;
import com.lukdut.monitoring.backend.repository.CommandRepository;
import com.lukdut.monitoring.backend.repository.SensorRepository;
import com.lukdut.monitoring.backend.rest.resources.CommandDto;
import com.lukdut.monitoring.gateway.dto.CommandState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.lukdut.monitoring.backend.integration.CommandIntegrationConfig.COMMANDS_TO_KAFKA_CHANNEL;

@RestController
@RequestMapping("/command")
public class CommandService {
    private static final Logger LOG = LoggerFactory.getLogger(CommandService.class);
    private final SensorRepository sensorRepository;
    private final CommandRepository commandRepository;
    private final MessageChannel commandsChannel;

    public CommandService(SensorRepository sensorRepository,
                          CommandRepository commandRepository,
                          @Qualifier(COMMANDS_TO_KAFKA_CHANNEL) MessageChannel commandsChannel) {
        this.sensorRepository = sensorRepository;
        this.commandRepository = commandRepository;
        this.commandsChannel = commandsChannel;
    }

    @PostMapping("/add")
    public Long add(@RequestBody CommandDto commandDto) {
        if (commandDto == null || commandDto.getImei() == 0 || commandDto.getCommand() == null) {
            return 0L;
        }
        if (sensorRepository.existsByImei(commandDto.getImei())) {
            try {
                Command command = new Command(commandDto.getImei(), commandDto.getCommand());
                commandRepository.save(command);
                commandsChannel.send(MessageBuilder.withPayload(command).build());
                LOG.info("New command registered for imei {}", command.getImei());
                return command.getId();
            } catch (Exception e) {
                LOG.warn("Can not register new command with imei={}", commandDto.getImei(), e);
            }
        } else {
            LOG.warn("Device with imei {} does not exists", commandDto.getImei());
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
