package com.alenut.yonder.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class ForecastDto {
  private String name;
  private int day;
  private int temperature;
  private int wind;
}