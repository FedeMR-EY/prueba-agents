package com.ey.app.clients.jikan.config;

import feign.Logger;
import feign.Request;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JikanClientConfig {

    @Bean
    public Logger.Level jikanLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options jikanRequestOptions() {
        return new Request.Options(10, TimeUnit.SECONDS, 30, TimeUnit.SECONDS, true);
    }
}
