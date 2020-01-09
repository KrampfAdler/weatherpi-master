package com.github.KrampfAdler.weatherpimaster.model.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="WEATHER_MEASUREMENT",uniqueConstraints = {
        @UniqueConstraint(columnNames = "ID"), })
public class WeatherMesurement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false, columnDefinition="BIGINT")
    private Long id;

    @Column(name = "REMOTE_ID", nullable = false, columnDefinition="BIGINT")
    private Long remoteId;

    @Column(name = "AMBIENT_TEMPERATURE", columnDefinition="Decimal(6,2)")
    private double ambientTemperature;

    @Column(name = "HUMIDITY", columnDefinition="Decimal(6,2)")
    private double humidity;

    @Column(name = "WIND_SPEED", columnDefinition="Decimal(6,2)")
    private double windSpeed;

    @Column(name = "WIND_GUST_SPEED", columnDefinition="Decimal(6,2)")
    private double windGustSpeed;

    @Column(name = "RAINFALL", columnDefinition="Decimal(6,2)")
    private double rainfall;

    @Column(name = "CREATED", columnDefinition="TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        id = id;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
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

    @Transient
    public String getTimeStamp(){
        if(created != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            return simpleDateFormat.format(created);
        }
        return null;
    }
}