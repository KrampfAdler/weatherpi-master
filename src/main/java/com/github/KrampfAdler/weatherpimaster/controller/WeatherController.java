package com.github.KrampfAdler.weatherpimaster.controller;


import com.github.KrampfAdler.weatherpimaster.dao.WeatherMesurementDao;
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
        return gson.toJson(weather);
    }

    @GetMapping(path = "/weather/today")
    public String getTempsToday(Model model){
        List<Map<Object,Object>> tempsDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> humitityDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> windSpeedDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> windSpeedGustDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> rainFallDataPoints = new ArrayList<Map<Object,Object>>();
        Calendar now = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        Calendar timeMemory = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        double rainPerHour = 0;
        for(WeatherMesurement weatherMesurement : weatherMesurementRepository.findAllByCreatedBetween(yesterday.getTime(), now.getTime())){
        //for(WeatherMesurement weatherMesurement : weatherMesurementRepository.findTop288ByOrderByIdDesc()){
            // Temperatures
            Map<Object,Object> tempsMap = new HashMap<Object,Object>();
            tempsMap.put("x", weatherMesurement.getCreated().getTime());
            tempsMap.put("y", weatherMesurement.getAmbientTemperature());
            tempsDataPoints.add(tempsMap);

            // Humidity
            Map<Object,Object> humidityMap = new HashMap<Object,Object>();
            humidityMap.put("x", weatherMesurement.getCreated().getTime());
            humidityMap.put("y", weatherMesurement.getHumidity());
            humitityDataPoints.add(humidityMap);

            // Wind speed
            Map<Object,Object> windSpeedMap = new HashMap<Object,Object>();
            windSpeedMap.put("x", weatherMesurement.getCreated().getTime());
            windSpeedMap.put("y", weatherMesurement.getWindSpeed());
            windSpeedDataPoints.add(windSpeedMap);

            // Wind gust speed
            Map<Object,Object> windGustSpeedMap = new HashMap<Object,Object>();
            windGustSpeedMap.put("x", weatherMesurement.getCreated().getTime());
            windGustSpeedMap.put("y", weatherMesurement.getWindGustSpeed());
            windSpeedGustDataPoints.add(windGustSpeedMap);

            Calendar date = Calendar.getInstance();
            date.setTime(weatherMesurement.getCreated());
            boolean sameHour = date.get(Calendar.HOUR_OF_DAY) == timeMemory.get(Calendar.HOUR_OF_DAY);
            if(sameHour) {
                rainPerHour += weatherMesurement.getRainfall();
            }else{
                Map<Object,Object> rainFallMap = new HashMap<Object,Object>();
                timeMemory.set(Calendar.MINUTE, 0);
                timeMemory.set(Calendar.SECOND, 0);
                rainFallMap.put("x", weatherMesurement.getCreated().getTime());
                rainFallMap.put("y", rainPerHour);
                rainFallDataPoints.add(rainFallMap);

                timeMemory.setTime(weatherMesurement.getCreated());
                rainPerHour = 0;
            }
        }
        model.addAttribute("tempPointsList", tempsDataPoints);
        model.addAttribute("humidityPointsList", humitityDataPoints);
        model.addAttribute("windSpeedsPointsList", windSpeedDataPoints);
        model.addAttribute("windGustSpeedsPointsList", windSpeedGustDataPoints);
        model.addAttribute("rainFallPointsList", rainFallDataPoints);


        WeatherMesurement newestWeatherMesurement = weatherMesurementRepository.findTopByOrderByIdDesc();
        Calendar minus1Hour = Calendar.getInstance();
        minus1Hour.add(Calendar.HOUR, -1);
        double rain = 0;
        for(WeatherMesurement weatherMesurement : weatherMesurementRepository.findAllByCreatedBetween(minus1Hour.getTime(), now.getTime())){
            rain += weatherMesurement.getRainfall();
        }

        WeatherMesurementDao weather = new WeatherMesurementDao(newestWeatherMesurement);
        weather.setRainfall(rain);

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
