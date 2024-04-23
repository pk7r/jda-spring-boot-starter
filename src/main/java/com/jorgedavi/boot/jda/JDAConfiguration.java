package com.jorgedavi.boot.jda;

import lombok.Data;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@Data
@ConfigurationProperties("spring.jda")
public class JDAConfiguration {

    private String botToken;

    private Long guildId;

    private OnlineStatus defaultStatus = OnlineStatus.ONLINE;

    private Set<CacheFlag> cacheFlags = new HashSet<>();

}
