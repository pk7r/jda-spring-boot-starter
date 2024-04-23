package com.jorgedavi.boot.jda;

import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

@AutoConfiguration
@EnableConfigurationProperties(JDAConfiguration.class)
public class JDAAutoConfiguration {

    @Bean
    @SneakyThrows
    @ConditionalOnMissingBean
    JDA jda(JDAConfiguration configuration) {
        return JDABuilder
                .createDefault(configuration.getBotToken())
                .enableCache(configuration.getCacheFlags())
                .enableIntents(configuration.getCacheFlags()
                        .stream()
                        .map(CacheFlag::getRequiredIntent)
                        .filter(Objects::nonNull)
                        .toList())
                .setStatus(configuration.getDefaultStatus())
                .build()
                .awaitReady();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.jda", name = "guild-id")
    Guild guild(JDA jda, JDAConfiguration configuration) {
        val guildId = configuration.getGuildId();
        return jda.getGuildById(guildId);
    }
}
