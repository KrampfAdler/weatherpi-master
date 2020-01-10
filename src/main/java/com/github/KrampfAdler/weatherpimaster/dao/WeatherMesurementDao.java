package com.github.KrampfAdler.weatherpimaster.dao;

import com.github.KrampfAdler.weatherpimaster.model.entity.WeatherMesurement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherMesurementDao {

    private double ambientTemperature;

    private double humidity;

    private double windSpeed;

    private double windGustSpeed;

    private double rainfall;

    private Date created;

    public WeatherMesurementDao(WeatherMesurement weatherMesurement) {
        this.setAmbientTemperature(weatherMesurement.getAmbientTemperature());
        this.setCreated(weatherMesurement.getCreated());
        this.setHumidity(weatherMesurement.getHumidity());
        this.setWindSpeed(weatherMesurement.getWindSpeed());
        this.setWindGustSpeed(weatherMesurement.getWindGustSpeed());
        this.setRainfall(weatherMesurement.getRainfall());
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTimeStamp(){
        if(created != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            return simpleDateFormat.format(created);
        }
        return null;
    }
}
