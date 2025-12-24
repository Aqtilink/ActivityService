package com.aqtilink.activity_service.messaging;

import com.aqtilink.activity_service.dto.NotificationEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisherActivity {

    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisherActivity(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(NotificationEventDTO notification) {
        // Sends the notification to the exchange with a routing key
        rabbitTemplate.convertAndSend(
                "notification-exchange",        // exchange name
                "notification.routingkey",      // routing key
                notification                     // payload
        );
    }
}
