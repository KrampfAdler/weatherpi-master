package com.github.KrampfAdler.weatherpimaster.model.repository;

import com.github.KrampfAdler.weatherpimaster.model.entity.WeatherMesurement;
import java.util.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface WeatherMesurementRepository extends CrudRepository<WeatherMesurement, Long>{

    List<WeatherMesurement> findAll();

    WeatherMesurement findTopByOrderByIdDesc();

    List<WeatherMesurement> findTop288ByOrderByIdDesc();

    List<WeatherMesurement> findAllByCreatedBetween(Date start, Date end);
}
