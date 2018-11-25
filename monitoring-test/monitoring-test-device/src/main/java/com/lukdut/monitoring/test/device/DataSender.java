package com.lukdut.monitoring.test.device;

import com.lukdut.monitoring.gateway.dto.IncomingSensorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Service
public class DataSender {
    private final OutputStream outputStream;

    DataSender(@Value("${test.host}") String host, @Value("${test.port}") int port) throws IOException {
        outputStream = new Socket(host, port).getOutputStream();
    }

    public void sendMessage(IncomingSensorMessage message) {
        try {
            outputStream.write(serializeMessage(message).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String serializeMessage(IncomingSensorMessage message) {
        return "{\"imei\":1}\r\n";
    }
}
