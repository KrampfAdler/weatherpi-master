package com.github.KrampfAdler.weatherpimaster.controller;


import com.github.KrampfAdler.weatherpimaster.dao.WeatherMeasurementDao;
import com.github.KrampfAdler.weatherpimaster.model.entity.WeatherMeasurement;
import com.github.KrampfAdler.weatherpimaster.model.repository.WeatherMeasurementRepository;
import com.google.gson.Gson;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
    private WeatherMeasurementRepository weatherMeasurementRepository;

    @GetMapping(path = "/weather")
    public String getWeather(Model model){
        WeatherMeasurementDao weather = new WeatherMeasurementDao(weatherMeasurementRepository.findTopByOrderByIdDesc());
        model.addAttribute("weather", weather);
        return "weather";
    }

    @GetMapping(path = "/weather/today")
    public String getTempsToday(Model model){
        List<Map<Object,Object>> tempsDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> humitityDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> windSpeedDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> windSpeedGustDataPoints = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> rainFallDataPoints = new ArrayList<Map<Object,Object>>();
        Instant now = Instant.now();
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        Calendar timeMemory = Calendar.getInstance();
        double rainPerHour = 0;
        for(WeatherMeasurement weatherMeasurement : weatherMeasurementRepository.findAllByCreatedBetween(Timestamp.from(yesterday), Timestamp.from(now))){
        //for(WeatherMeasurement weatherMeasurement : weatherMeasurementRepository.findTop288ByOrderByIdDesc()){
            // Temperatures
            Map<Object,Object> tempsMap = new HashMap<Object,Object>();
            tempsMap.put("x", weatherMeasurement.getCreated());
            tempsMap.put("y", weatherMeasurement.getAmbientTemperature());
            tempsDataPoints.add(tempsMap);

            // Humidity
            Map<Object,Object> humidityMap = new HashMap<Object,Object>();
            humidityMap.put("x", weatherMeasurement.getCreated().getTime());
            humidityMap.put("y", weatherMeasurement.getHumidity());
            humitityDataPoints.add(humidityMap);

            // Wind speed
            Map<Object,Object> windSpeedMap = new HashMap<Object,Object>();
            windSpeedMap.put("x", weatherMeasurement.getCreated().getTime());
            windSpeedMap.put("y", weatherMeasurement.getWindSpeed());
            windSpeedDataPoints.add(windSpeedMap);

            // Wind gust speed
            Map<Object,Object> windGustSpeedMap = new HashMap<Object,Object>();
            windGustSpeedMap.put("x", weatherMeasurement.getCreated().getTime());
            windGustSpeedMap.put("y", weatherMeasurement.getWindGustSpeed());
            windSpeedGustDataPoints.add(windGustSpeedMap);

            Calendar date = Calendar.getInstance();
            date.setTime(weatherMeasurement.getCreated());
            boolean sameHour = date.get(Calendar.HOUR_OF_DAY) == timeMemory.get(Calendar.HOUR_OF_DAY);
            if(sameHour) {
                rainPerHour += weatherMeasurement.getRainfall();
            }else{
                Map<Object,Object> rainFallMap = new HashMap<Object,Object>();
                timeMemory.set(Calendar.MINUTE, 0);
                timeMemory.set(Calendar.SECOND, 0);
                rainFallMap.put("x", weatherMeasurement.getCreated().getTime());
                rainFallMap.put("y", rainPerHour);
                rainFallDataPoints.add(rainFallMap);

                timeMemory.setTime(weatherMeasurement.getCreated());
                rainPerHour = 0;
            }
        }
        model.addAttribute("tempPointsList", tempsDataPoints);
        model.addAttribute("humidityPointsList", humitityDataPoints);
        model.addAttribute("windSpeedsPointsList", windSpeedDataPoints);
        model.addAttribute("windGustSpeedsPointsList", windSpeedGustDataPoints);
        model.addAttribute("rainFallPointsList", rainFallDataPoints);


        WeatherMeasurement newestWeatherMeasurement = weatherMeasurementRepository.findTopByOrderByIdDesc();
        Instant minus1Hour = Instant.now().minus(1, ChronoUnit.HOURS);
        double rain = 0;
        for(WeatherMeasurement weatherMeasurement : weatherMeasurementRepository.findAllByCreatedBetween(Timestamp.from(minus1Hour), Timestamp.from(now))){
            rain += weatherMeasurement.getRainfall();
        }

        WeatherMeasurementDao weather = new WeatherMeasurementDao(newestWeatherMeasurement);
        weather.setRainfall(rain);

        model.addAttribute("weather", weather);
        return "today";
    }

    @GetMapping(path = "/weather/month")
    public String getGraph(Model model){
        List<Map<Object,Object>> highDataPoints1 = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> lowDataPoints1 = new ArrayList<Map<Object,Object>>();
        List<Map<Object,Object>> rainDataPoints1 = new ArrayList<Map<Object,Object>>();


        Instant now = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        System.out.println("now: " + now);
        Instant nowMinusMonth = now.minus(31, ChronoUnit.DAYS);

        List<WeatherMeasurement> weatherMeasurements = weatherMeasurementRepository.findAllByCreatedBetween(Timestamp.from(nowMinusMonth), Timestamp.from(now));

        Instant lastDay = nowMinusMonth;
        List<WeatherMeasurement> measurementsPerDay = new ArrayList<>();

        Iterator<WeatherMeasurement> iterator = weatherMeasurements.iterator();
        while (iterator.hasNext()) {
            WeatherMeasurement weatherMeasurement = iterator.next();
            if(weatherMeasurement.getCreated().toInstant().truncatedTo(ChronoUnit.DAYS).equals(lastDay) && iterator.hasNext()){
                measurementsPerDay.add(weatherMeasurement);
            }else{
                //TODO: This doesn't work with timezones 2020-04-28 02:02:51.0 will be the next day because of summer time, fix later
                Map<Object, Object> highMap = new HashMap<Object, Object>();
                highMap.put("y", Collections.max(measurementsPerDay, new compTemps()).getAmbientTemperature());
                Map<Object, Object> lowMap = new HashMap<Object, Object>();
                lowMap.put("y", Collections.min(measurementsPerDay, new compTemps()).getAmbientTemperature());
                Map<Object, Object> rainMap = new HashMap<Object, Object>();
                rainMap.put("y", measurementsPerDay.stream().mapToDouble(WeatherMeasurement::getRainfall).sum());

                ZonedDateTime zdt = ZonedDateTime.ofInstant(lastDay, ZoneId.systemDefault());
                Calendar calendar = GregorianCalendar.from(zdt);

                highMap.put("x", calendar.getTime().getTime());
                lowMap.put("x", calendar.getTime().getTime());
                rainMap.put("x", calendar.getTime().getTime());

                highDataPoints1.add(highMap);
                lowDataPoints1.add(lowMap);
                rainDataPoints1.add(rainMap);

                measurementsPerDay = new ArrayList<>();
                lastDay = weatherMeasurement.getCreated().toInstant().truncatedTo(ChronoUnit.DAYS);
                measurementsPerDay.add(weatherMeasurement);
            }
        }




        model.addAttribute("highDataPointsList", highDataPoints1);
        model.addAttribute("lowDataPointsList", lowDataPoints1);
        model.addAttribute("rainFallPointsList", rainDataPoints1);
        return "month";
    }

    public class compTemps implements Comparator<WeatherMeasurement> {
        public int compare(WeatherMeasurement a, WeatherMeasurement b) {
            if (a.getAmbientTemperature() > b.getAmbientTemperature())
                return -1; // highest value first
            if (a.getAmbientTemperature() == b.getAmbientTemperature())
                return 0;
            return 1;
        }
    }

    @GetMapping(path = "/weather/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getWeathers(@PathVariable String id) {
        Gson gson = new Gson();
        WeatherMeasurement weather = weatherMeasurementRepository.findById(Long.valueOf(id)).orElse(null);
        String json = gson.toJson(weather);
        return json;
    }
}
