package com.alenut.yonder.api.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("app")
public class AppConfig {

  private String forecastApi;

  private List<String> availableCities;
}
