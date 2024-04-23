package com.jorgedavi.boot.jda.event;

import com.jorgedavi.boot.jda.JDAAutoConfiguration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;

@AutoConfiguration
@AutoConfigureAfter(JDAAutoConfiguration.class)
public class JDAEventPublisherAutoConfiguration {

    private final ApplicationEventPublisher eventPublisher;

    public JDAEventPublisherAutoConfiguration(JDA jda, ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        jda.addEventListener((EventListener) e -> publishEvent(jda, e));
    }

    private <E extends GenericEvent> void publishEvent(JDA jda, E event) {
        eventPublisher.publishEvent(new PayloadApplicationEvent<>(jda, event));
    }
}