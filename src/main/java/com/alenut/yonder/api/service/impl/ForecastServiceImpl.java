package com.alenut.yonder.api.service.impl;

import com.alenut.yonder.api.config.AppConfig;
import com.alenut.yonder.api.domain.ForecastDto;
import com.alenut.yonder.api.domain.MeteoDto;
import com.alenut.yonder.api.service.ForecastService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@Validated
public class ForecastServiceImpl implements ForecastService {

  private final WebClient webClient;

  public ForecastServiceImpl(AppConfig appConfig, WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl(appConfig.getForecastApi()).build();
  }

  @Override
  public Flux<ForecastDto> getForecastForCity(@NotEmpty String city) {
    return webClient.get()
        .uri(String.format("/%s", city))
        .retrieve()
        .bodyToFlux(MeteoDto.class)
        .onErrorResume(error -> Flux.empty())
        .flatMap(meteoDto -> Flux.fromStream(
            meteoDto.forecast()
                .stream()
                .peek(forecast -> forecast.setName(city))));
  }
}
