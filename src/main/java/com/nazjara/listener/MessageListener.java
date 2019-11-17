package com.nazjara.listener;

import com.nazjara.config.JmsConfig;
import com.nazjara.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.MY_QUEUE)
    public void listen(@Payload Message messageToListen,
                       @Headers MessageHeaders headers, javax.jms.Message message) {
        log.info("Message received: " + messageToListen);
    }

    @JmsListener(destination = JmsConfig.MY_SEND_RECEIVE_QUEUE)
    public void listen2(@Payload Message messageToListen,
                       @Headers MessageHeaders headers, javax.jms.Message message) throws JMSException {
        log.info("Message received: " + messageToListen);

        Message messageToReply = Message.builder()
                .id(UUID.randomUUID())
                .message("That's message to reply")
                .build();

        jmsTemplate.convertAndSend(message.getJMSReplyTo(), messageToReply);
    }
}
