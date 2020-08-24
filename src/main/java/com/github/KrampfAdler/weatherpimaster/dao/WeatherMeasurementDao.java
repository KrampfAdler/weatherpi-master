package com.github.KrampfAdler.weatherpimaster.dao;

import com.github.KrampfAdler.weatherpimaster.model.entity.WeatherMeasurement;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherMeasurementDao {

    private double ambientTemperature;

    private double humidity;

    private double windSpeed;

    private double windGustSpeed;

    private double rainfall;

    private Timestamp created;

    private String date;

    public WeatherMeasurementDao(WeatherMeasurement weatherMeasurement) {
        this.setAmbientTemperature(weatherMeasurement.getAmbientTemperature());
        this.setCreated(weatherMeasurement.getCreated());
        this.setHumidity(weatherMeasurement.getHumidity());
        this.setWindSpeed(weatherMeasurement.getWindSpeed());
        this.setWindGustSpeed(weatherMeasurement.getWindGustSpeed());
        this.setRainfall(weatherMeasurement.getRainfall());

        Date date = new Date();
        date.setTime(weatherMeasurement.getCreated().getTime());
        this.setDate(new SimpleDateFormat("yyyy-MM-dd").format(date));
    }

    public double getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setAmbientTemperature(double ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindGustSpeed() {
        return windGustSpeed;
    }

    public void setWindGustSpeed(double windGustSpeed) {
        this.windGustSpeed = windGustSpeed;
    }

    public double getRainfall() {
        return rainfall;
    }

    public void setRainfall(double rainfall) {
        this.rainfall = rainfall;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeStamp(){
        if(created != null){
            Date date = new Date(created.getTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            return simpleDateFormat.format(date);
        }
        return null;
    }
}
