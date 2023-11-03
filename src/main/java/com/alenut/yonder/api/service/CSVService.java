package com.alenut.yonder.api.service;

import com.alenut.yonder.api.domain.CityDto;
import reactor.core.publisher.Flux;

public interface CSVService {
  void writeWeatherToCSV(Flux<CityDto> weatherForCities);
}
