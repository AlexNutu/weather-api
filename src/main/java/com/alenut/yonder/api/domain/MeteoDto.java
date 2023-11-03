package com.alenut.yonder.api.domain;

import java.util.List;

public record MeteoDto(String temperature, String wind, String description, List<ForecastDto> forecast) {

}
