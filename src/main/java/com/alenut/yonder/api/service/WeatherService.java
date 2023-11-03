package com.alenut.yonder.api.service;

import com.alenut.yonder.api.domain.CityDto;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;

@Validated
public interface WeatherService {

  Flux<CityDto> getWeatherForCities(@NotEmpty List<String> cities);
}
