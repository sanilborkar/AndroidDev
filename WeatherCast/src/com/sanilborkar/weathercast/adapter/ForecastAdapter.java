package com.sanilborkar.weathercast.adapter;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sanilborkar.weathercast.data.DayForecast;
import com.sanilborkar.weathercast.data.ForecastData;
import com.sanilborkar.weathercast.ForecastFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class ForecastAdapter extends FragmentPagerAdapter {

	private int numDays;
	private FragmentManager fm;
	private ForecastData forecast;
	private final static SimpleDateFormat sdf = new SimpleDateFormat("E, dd-MM");
	
	public ForecastAdapter(int numDays, FragmentManager fm, ForecastData forecast) {
		super(fm);
		this.numDays = numDays;
		this.fm = fm;
		this.forecast = forecast;
		
	}
	
	
	// Page title
	@Override
	public CharSequence getPageTitle(int position) {
		// We calculate the next days adding position to the current date
		Date d = new Date();
		Calendar gc =  new GregorianCalendar();
		gc.setTime(d);
		gc.add(GregorianCalendar.DAY_OF_MONTH, position);
		
		return sdf.format(gc.getTime());
		
		
	}



	@Override
	public Fragment getItem(int num) {
		DayForecast dayForecast = (DayForecast) forecast.getForecast(num);
		ForecastFragment f = new ForecastFragment();
		f.setForecast(dayForecast);
		return f;
	}


	@Override
	public int getCount() {
		
		return numDays;
	}

}
