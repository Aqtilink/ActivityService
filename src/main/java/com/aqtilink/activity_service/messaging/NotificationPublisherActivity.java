package com.aqtilink.activity_service.messaging;

import com.aqtilink.activity_service.dto.NotificationEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

// Publishes notification events to RabbitMQ

@Component
public class NotificationPublisherActivity {

    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisherActivity(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(NotificationEventDTO notification) {
        rabbitTemplate.convertAndSend(
                "notification-exchange",
                "notification.routingkey",
                notification
        );
    }
}
