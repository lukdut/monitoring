package com.lukdut.monitoring.test.device;

import com.lukdut.monitoring.gateway.dto.IncomingSensorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Collections;

@ShellComponent
public class ShellService {
    private static final Logger LOG = LoggerFactory.getLogger(ShellService.class);
    private final TaskManager taskManager;

    public ShellService(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @ShellMethod("Load messages source file")
    public void load(@ShellOption String fileName) {
        //TODO: create message list from file
        taskManager.updateDataSet(Collections.singletonList(new IncomingSensorMessage()));
    }

    @ShellMethod("Start data producing with the specified speed (in messages per second, default is 1)")
    public void start(@ShellOption(defaultValue = "1") int speed) {
        LOG.info("Starting data producing with speed {}", speed);
        taskManager.start(speed);
    }

    @ShellMethod("Stop data producing")
    public void stop() {
        taskManager.stop();
    }

    @ShellMethod("Change data producing speed (in messages per second)")
    public void speed(@ShellOption(defaultValue = "-1") Integer newSpeed) {
        if (newSpeed < 0) {
            System.out.println(taskManager.getActualSpeed());
        } else if (newSpeed == 0) {
            taskManager.stop();
        } else {
            taskManager.setSpeed(newSpeed);
        }
    }
}
