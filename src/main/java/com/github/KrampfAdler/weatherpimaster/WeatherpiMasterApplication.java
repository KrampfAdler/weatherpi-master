package com.github.KrampfAdler.weatherpimaster;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(
		basePackages = { "com.github.KrampfAdler.weatherpimaster.model.repository" })
@ComponentScan(
		basePackages = { "com.github.KrampfAdler.weatherpimaster.controller" })
@EntityScan(
		basePackages = { "com.github.KrampfAdler.weatherpimaster.model.entity" })
@EnableScheduling
@EnableCaching
public class WeatherpiMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherpiMasterApplication.class, args);
	}

	@PostConstruct
	public void init(){
		// Setting Spring Boot SetTimeZone
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
		System.out.println("Startup Time : " + new Date().toString());
	}
}
