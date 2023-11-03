package com.alenut.yonder.api.service.impl;

import static java.util.stream.Collectors.toCollection;

import com.alenut.yonder.api.config.AppConfig;
import com.alenut.yonder.api.domain.CityDto;
import com.alenut.yonder.api.domain.ForecastDto;
import com.alenut.yonder.api.service.ForecastService;
import com.alenut.yonder.api.service.WeatherService;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Validated
public class WeatherServiceImpl implements WeatherService {

  private final AppConfig appConfig;
  private final ForecastService forecastService;

  @Autowired
  public WeatherServiceImpl(AppConfig appConfig, ForecastService forecastService) {
    this.appConfig = appConfig;
    this.forecastService = forecastService;
  }

  @Override
  public Flux<CityDto> getWeatherForCities(@NotEmpty List<String> cities) {
    List<String> availableCities = appConfig.getAvailableCities();
    Set<String> filteredCities = cities.stream()
        .filter(availableCities::contains)
        .collect(toCollection(TreeSet::new));

    return Flux.fromIterable(filteredCities)
        .map(forecastService::getForecastForCity)
        .flatMap(this::getWeatherForCity)
        .sort(Comparator.comparing(CityDto::name));
  }

  private Mono<CityDto> getWeatherForCity(Flux<ForecastDto> forecastFlux) {
    return forecastFlux
        .reduce(new CityDto("", BigDecimal.ZERO, BigDecimal.ZERO), (city, forecast) -> {
          BigDecimal temperatureSum = city.temperature().add(BigDecimal.valueOf(forecast.getTemperature()));
          BigDecimal windSum = city.wind().add(BigDecimal.valueOf(forecast.getWind()));
          return new CityDto(forecast.getName(), temperatureSum, windSum);
        })
        .flatMap(city -> forecastFlux
            .count()
            .map(count -> computeAverages(city, count)));
  }

  private CityDto computeAverages(CityDto city, Long count) {
    long nonNullCount = count != null ? count : 0L;
    BigDecimal averageTemperature = nonNullCount > 0
        ? city.temperature().divide(BigDecimal.valueOf(nonNullCount), 2, RoundingMode.HALF_UP)
        : BigDecimal.ZERO;
    BigDecimal averageWind = nonNullCount > 0
        ? city.wind().divide(BigDecimal.valueOf(nonNullCount), 2, RoundingMode.HALF_UP)
        : BigDecimal.ZERO;
    return new CityDto(city.name(), averageTemperature, averageWind);
  }
}
