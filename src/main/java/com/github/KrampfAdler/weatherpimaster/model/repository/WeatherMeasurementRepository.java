package com.github.KrampfAdler.weatherpimaster.model.repository;

import com.github.KrampfAdler.weatherpimaster.model.entity.WeatherMeasurement;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface WeatherMeasurementRepository extends CrudRepository<WeatherMeasurement, Long>{

    List<WeatherMeasurement> findAll();

    WeatherMeasurement findTopByOrderByIdDesc();

    List<WeatherMeasurement> findTop288ByOrderByIdDesc();

    List<WeatherMeasurement> findAllByCreatedBetween(Timestamp start, Timestamp end);
}
