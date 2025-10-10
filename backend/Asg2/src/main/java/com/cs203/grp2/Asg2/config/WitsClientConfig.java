package com.cs203.grp2.Asg2.config;


import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class WitsClientConfig {
  @Bean
  WebClient witsWebClient() {
    HttpClient http = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
        .responseTimeout(Duration.ofSeconds(45));
    return WebClient.builder()
        .baseUrl("https://wits.worldbank.org")
        .clientConnector(new ReactorClientHttpConnector(http))
        .build();
  }
}
