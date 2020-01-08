package com.github.KrampfAdler.weatherpimaster.controller;


import com.github.KrampfAdler.weatherpimaster.model.entity.WeatherMesurement;
import com.github.KrampfAdler.weatherpimaster.model.repository.WeatherMesurementRepository;
import com.google.gson.Gson;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WeatherController {



    @Autowired
    private WeatherMesurementRepository weatherMesurementRepository;

    @GetMapping(path = "/weather")
    public String getWeather(Model model){
        Gson gson = new Gson();
        WeatherMesurement weather = weatherMesurementRepository.findTopByOrderByIdDesc();
        String json = gson.toJson(weather);
        model.addAttribute("weather", weather);
        return "weather";
    }

    @GetMapping(path = "/weather/tempsToday")
    public String getGraph(Model model){
        List<Map<Object,Object>> tempsdataPoints1 = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> humititydataPoints1 = new ArrayList<Map<Object,Object>>();
        Calendar now = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        for(WeatherMesurement weatherMesurement : weatherMesurementRepository.findAllByCreatedBetween(yesterday.getTime(), now.getTime())){
            Map<Object,Object> map = new HashMap<Object,Object>();
            map.put("x", weatherMesurement.getCreated().getTime());
            map.put("y", weatherMesurement.getAmbientTemperature());
            tempsdataPoints1.add(map);
            Map<Object,Object> map2 = new HashMap<Object,Object>();
            map2.put("x", weatherMesurement.getCreated().getTime());
            map2.put("y", weatherMesurement.getHumidity());
            humititydataPoints1.add(map2);

        }
        model.addAttribute("tempPointsList", tempsdataPoints1);
        model.addAttribute("humidityPointsList", humititydataPoints1);
        return "tempsToday";
    }

    @GetMapping(path = "/weather/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getWeathers(@PathVariable String id) {
        Gson gson = new Gson();
        WeatherMesurement weather = weatherMesurementRepository.findById(Long.valueOf(id)).orElse(null);
        String json = gson.toJson(weather);
        return json;
    }
}
