package com.alenut.yonder.api.domain;

import java.math.BigDecimal;

public record CityDto(String name, BigDecimal temperature, BigDecimal wind) {

}
