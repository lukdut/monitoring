package com.lukdut.monitoring.test.device;

import com.lukdut.monitoring.gateway.dto.IncomingSensorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskManager {
    private static final Logger LOG = LoggerFactory.getLogger(TaskManager.class);
    private final ExecutorService stubDataProducerPool =
            Executors.newScheduledThreadPool(1, r -> new Thread(r, "Stub data producer"));
    private final DataSender dataSender;

    private DataProducer task;
    private volatile List<IncomingSensorMessage> dataSource = Collections.singletonList(new IncomingSensorMessage());
    private AtomicInteger actualSpeed = new AtomicInteger();

    TaskManager(DataSender dataSender) {
        this.dataSender = dataSender;
    }

    public synchronized void updateDataSet(List<IncomingSensorMessage> data) {
        dataSource = data;
    }

    public synchronized void start(int speed) {
        if (task != null) {
            task.close();
        }
        task = new DataProducer(new ArrayList<>(dataSource), speed, actualSpeed);
        stubDataProducerPool.submit(task);
    }

    public synchronized void stop() {
        if (task != null) {
            task.close();
        }
    }

    public synchronized void setSpeed(int newSpeed) {
        if (task != null) {
            task.setSpeed(newSpeed);
        }
    }

    public int getActualSpeed() {
        return actualSpeed.get();
    }

    class DataProducer implements Runnable, Closeable {
        private final List<IncomingSensorMessage> dataSource;
        private volatile boolean isRunning = true;
        private volatile int speed;
        private final AtomicInteger actualSpeed;
        private volatile Thread thread;
        int counter = 0;

        DataProducer(List<IncomingSensorMessage> dataSource, int desiredSpeed, AtomicInteger actualSpeed) {
            this.dataSource = dataSource;
            this.speed = desiredSpeed;
            this.actualSpeed = actualSpeed;
        }

        public void setSpeed(int newSpeed) {
            speed = newSpeed;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (thread == null || !thread.isAlive()) {
                    actualSpeed.set(0);
                    thread = new Thread(() -> {
                        for (int i = 0; i < speed && isRunning; i++) {
                            dataSender.sendMessage(dataSource.get(counter));
                            counter++;
                            actualSpeed.incrementAndGet();
                            if (dataSource.size() == counter) {
                                counter = 0;
                            }
                        }
                    });
                    thread.start();
                } else {
                    System.out.println("Max speed reached: " + actualSpeed.get());
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void close() {
            isRunning = false;
        }
    }
}
