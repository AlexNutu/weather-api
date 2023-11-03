package com.alenut.yonder.api.controller;


import static com.alenut.yonder.api.common.ApiPrefix.API;

import com.alenut.yonder.api.domain.CityDto;
import com.alenut.yonder.api.domain.WeatherDto;
import com.alenut.yonder.api.service.CSVService;
import com.alenut.yonder.api.service.WeatherService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(API)
public class WeatherController {

  private final WeatherService weatherService;
  private final CSVService csvService;

  @Autowired
  public WeatherController(WeatherService weatherService, CSVService csvService) {
    this.weatherService = weatherService;
    this.csvService = csvService;
  }

  @GetMapping("/weather")
  public Mono<WeatherDto> get(@RequestParam(name = "city") List<String> cities) {
    Flux<CityDto> weatherForCities = weatherService.getWeatherForCities(cities);

    csvService.writeWeatherToCSV(weatherForCities);

    return weatherForCities
        .collectList()
        .map(WeatherDto::new);
  }
}
