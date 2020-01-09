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

    @GetMapping(path = "/weather/today")
    public String getTempsToday(Model model){
        List<Map<Object,Object>> tempsDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> humitityDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> windSpeedDataPoints = new ArrayList<Map<Object,Object>>();
        Calendar now = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        //for(WeatherMesurement weatherMesurement : weatherMesurementRepository.findAllByCreatedBetween(yesterday.getTime(), now.getTime())){
        for(WeatherMesurement weatherMesurement : weatherMesurementRepository.findTop288ByOrderByIdDesc()){
            Map<Object,Object> tempsMap = new HashMap<Object,Object>();
            tempsMap.put("x", weatherMesurement.getCreated().getTime());
            tempsMap.put("y", weatherMesurement.getAmbientTemperature());
            tempsDataPoints.add(tempsMap);
            Map<Object,Object> humidityMap = new HashMap<Object,Object>();
            humidityMap.put("x", weatherMesurement.getCreated().getTime());
            humidityMap.put("y", weatherMesurement.getHumidity());
            humitityDataPoints.add(humidityMap);
            Map<Object,Object> windSpeedMap = new HashMap<Object,Object>();
            windSpeedMap.put("x", weatherMesurement.getCreated().getTime());
            windSpeedMap.put("y", weatherMesurement.getWindSpeed());
            windSpeedDataPoints.add(windSpeedMap);

        }
        model.addAttribute("tempPointsList", tempsDataPoints);
        model.addAttribute("humidityPointsList", humitityDataPoints);
        model.addAttribute("windSpeedsPointsList", windSpeedDataPoints);
        WeatherMesurement weather = weatherMesurementRepository.findTopByOrderByIdDesc();
        model.addAttribute("weather", weather);
        return "today";
    }

    @GetMapping(path = "/weather/tempsMonth")
    public String getGraph(Model model){
        List<Map<Object,Object>> highDataPoints1 = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> lowDataPoints1 = new ArrayList<Map<Object,Object>>();
        Calendar now = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.MONTH, -1);
        double dailyHighest = -100;
        double dailyLowest = 100;
        Calendar day = Calendar.getInstance();
        for(WeatherMesurement weatherMesurement : weatherMesurementRepository.findAllByCreatedBetween(yesterday.getTime(), now.getTime())){
            Calendar date = Calendar.getInstance();
            date.setTime(weatherMesurement.getCreated());
            boolean sameDay = date.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR) &&
                    date.get(Calendar.YEAR) == day.get(Calendar.YEAR);
            if(sameDay) {
                if(weatherMesurement.getAmbientTemperature() > dailyHighest){
                    dailyHighest = weatherMesurement.getAmbientTemperature();
                }
                if(weatherMesurement.getAmbientTemperature() < dailyLowest){
                    dailyLowest = weatherMesurement.getAmbientTemperature();
                }
            }else{
                day = date;
                if(dailyHighest != -100) {
                    Map<Object, Object> map = new HashMap<Object, Object>();
                    map.put("x", day.getTime());
                    map.put("y", dailyHighest);
                    highDataPoints1.add(map);
                }
                if(dailyLowest != 100) {
                    Map<Object, Object> map2 = new HashMap<Object, Object>();
                    map2.put("x", day.getTime());
                    map2.put("y", dailyLowest);
                    lowDataPoints1.add(map2);
                }
                dailyHighest = -100;
                dailyLowest = 100;
                day.setTime(weatherMesurement.getCreated());

            }
        }
        model.addAttribute("highDataPointsList", highDataPoints1);
        model.addAttribute("lowDataPointsList", lowDataPoints1);
        return "tempsMonth";
    }

    @GetMapping(path = "/weather/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getWeathers(@PathVariable String id) {
        Gson gson = new Gson();
        WeatherMesurement weather = weatherMesurementRepository.findById(Long.valueOf(id)).orElse(null);
        String json = gson.toJson(weather);
        return json;
    }
}