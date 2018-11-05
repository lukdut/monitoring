package com.lukdut.monitoring.gateway;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import static com.lukdut.monitoring.gateway.integration.TcpRequestIntegrationConfig.RAW_INPUT_CHANNEL;

@RunWith(SpringRunner.class)
@SpringBootTest
@Configuration
public class KafkaSavingTests {
    private static volatile boolean isSaved = false;

    @Qualifier(RAW_INPUT_CHANNEL)
    @Autowired
    MessageChannel rawInputChannel;

    @Configuration
    @Import({MonitoringGatewayApplication.class}) // the actual configuration
    public static class TestConfig {
        @Bean
        public MessageHandler kafkaSaver() {
            return message -> isSaved = true;
        }
    }

    @Test
    @Ignore
    public void correctDataStores() throws InterruptedException {
        rawInputChannel.send(MessageBuilder.withPayload("{\"imei\":2}".getBytes()).build());

        for (int i = 0; i < 10; i++) {
            if (isSaved) {
                return;
            }
            Thread.sleep(100);
        }
        Assert.fail();
    }
}
