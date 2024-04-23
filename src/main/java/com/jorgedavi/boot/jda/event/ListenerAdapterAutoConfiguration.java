package com.jorgedavi.boot.jda.event;

import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;

@AutoConfiguration
@AutoConfigureAfter(JDAEventPublisherAutoConfiguration.class)
public class ListenerAdapterAutoConfiguration {

    public ListenerAdapterAutoConfiguration(ApplicationContext context, JDA jda) {
        val listenerAdapters = context.getBeansOfType(ListenerAdapter.class).values();
        listenerAdapters.forEach(jda::addEventListener);
    }
}