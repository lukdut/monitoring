package com.lukdut.monitoring.backend.repository;

import com.lukdut.monitoring.backend.model.Sensor;
import com.lukdut.monitoring.backend.model.SensorMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DataRepositoryTest {

    @Autowired
    DataRepository dataRepository;

    @Autowired
    SensorRepository sensorRepository;

    @Test
    public void lastDataTest() {
        System.out.println("test");

        List<SensorMessage> all;
        all = dataRepository.findLastData(Arrays.asList(1L, 2L));
        Assert.assertEquals(2, all.size());

        all = dataRepository.findLastData(Collections.singletonList(1L));
        Assert.assertEquals(1, all.size());
        Assert.assertEquals("data3", all.get(0).getData());

        all = dataRepository.findLastData(Collections.singletonList(2L));
        Assert.assertEquals(1, all.size());
        Assert.assertEquals("data4", all.get(0).getData());
    }

    @Test
    public void logTest() {
        Iterable<SensorMessage> all = dataRepository.findAll();
        SensorMessage middleMsg = null;
        for (SensorMessage msg : all) {
            if (msg.getData().equals("data2")) {
                middleMsg = msg;
                break;
            }
        }

        Assert.assertNotNull(middleMsg);
        List<SensorMessage> dataLog = dataRepository.findDataLog(1L,
                new Date(middleMsg.getTimestamp().getTime() - 50),
                new Date(middleMsg.getTimestamp().getTime() + 50));

        Assert.assertEquals(1, dataLog.size());
        Assert.assertEquals("data2", dataLog.get(0).getData());

        dataLog = dataRepository.findDataLog(1L,
                new Date(0),
                new Date(System.currentTimeMillis()));
        Assert.assertEquals(3, dataLog.size());
        Assert.assertTrue(dataLog.get(0).getTimestamp().before(dataLog.get(2).getTimestamp()));

        dataLog = dataRepository.findDataLog(42L,
                new Date(0),
                new Date(System.currentTimeMillis()));
        Assert.assertEquals(0, dataLog.size());
    }


    @Before
    public void before() throws InterruptedException {
        sensorRepository.save(new Sensor(1L));
        sensorRepository.save(new Sensor(2L));
        sensorRepository.save(new Sensor(3L));

        dataRepository.save(createMessage(1, "data1"));
        Thread.sleep(100);
        dataRepository.save(createMessage(1, "data2"));
        Thread.sleep(100);
        dataRepository.save(createMessage(1, "data3"));

        dataRepository.save(createMessage(2, "data4"));
        dataRepository.save(createMessage(3, null));
    }

    private SensorMessage createMessage(long imei, String data) {
        SensorMessage sensorMessage = new SensorMessage();
        sensorMessage.setImei(imei);
        sensorMessage.setData(data);
        return sensorMessage;
    }

}
