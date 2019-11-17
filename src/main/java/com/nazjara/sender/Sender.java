package com.nazjara.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nazjara.config.JmsConfig;
import com.nazjara.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class Sender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper mapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        log.info("Sending a message...");

        Message message = Message.builder()
                .id(UUID.randomUUID())
                .message("That's message")
                .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);

        log.info("Message sent!");
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {
        log.info("Sending a message...");

        Message message = Message.builder()
                .id(UUID.randomUUID())
                .message("That's message")
                .build();

        javax.jms.Message receivedMessage = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RECEIVE_QUEUE, session -> {
            try {
                javax.jms.Message jmsMessage = session.createTextMessage(mapper.writeValueAsString(message));
                jmsMessage.setStringProperty("_type", "com.nazjara.model.Message");

                return jmsMessage;
            } catch (JsonProcessingException e) {
                throw new JMSException("Sending message failed");
            }
        });

        log.info("Message sent and received!");
        log.info("Message received: " + receivedMessage.getBody(String.class));
    }
}
