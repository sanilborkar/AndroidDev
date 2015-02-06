package com.sanilborkar.weathercast.data;

import java.util.Date;
import java.util.List;



public class ForecastData {
	
	private List<DayForecast> daysForecast;

	public void addForecast(DayForecast forecast) {
		daysForecast.add(forecast);
		System.out.println("Add forecast ["+forecast+"]");
	}
	
	public DayForecast getForecast(int dayNum) {
		return daysForecast.get(dayNum);
	}
	
}
