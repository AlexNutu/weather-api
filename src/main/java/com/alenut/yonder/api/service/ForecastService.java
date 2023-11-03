package com.alenut.yonder.api.service;

import com.alenut.yonder.api.domain.ForecastDto;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;

@Validated
public interface ForecastService {

  Flux<ForecastDto> getForecastForCity(@NotEmpty String city);
}
