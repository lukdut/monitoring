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


    @Before
    public void before() throws InterruptedException {
        sensorRepository.save(new Sensor(1L));
        sensorRepository.save(new Sensor(2L));
        sensorRepository.save(new Sensor(3L));

        dataRepository.save(createMessage(1, "data1"));
        Thread.sleep(1);
        dataRepository.save(createMessage(1, "data2"));
        Thread.sleep(1);
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
