package com.alenut.yonder.api.service.impl;

import com.alenut.yonder.api.domain.CityDto;
import com.alenut.yonder.api.service.CSVService;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class CSVServiceImpl implements CSVService {

  private static final Logger log = LoggerFactory.getLogger(CSVServiceImpl.class);

  @Value("${app.csvFilePath}")
  private String csvFilePath;

  @Override
  public void writeWeatherToCSV(Flux<CityDto> weatherForCities) {
    try (FileWriter fileWriter = new FileWriter("src/main/resources/" + csvFilePath)) {

      String[] header = {"Name", "Temperature", "Wind"};
      fileWriter.append(String.join(",", header)).append("\n");

      weatherForCities
          .concatMap(cityDto -> {
            String[] row = {cityDto.name(), cityDto.temperature().toString(), cityDto.wind().toString()};

            return Mono.just(row)
                .publishOn(Schedulers.boundedElastic()) // Change the context for file writing
                .doOnNext(dataToWrite -> {
                  try {
                    fileWriter.append(String.join(",", dataToWrite)).append("\n");
                  } catch (IOException e) {
                    log.error("Error writing line to CSV file: {}", e.getMessage());
                  }
                });
          })
          .then()
          .block(); // Block until all items are processed so the file writer is not closed prematurely

      log.info("Data was written to CSV file at: {}", csvFilePath);
    } catch (IOException e) {
      log.error("Error when opening CSV file: {}", e.getMessage());
    }
  }
}

