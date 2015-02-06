package com.sanilborkar.weathercast;


import com.sanilborkar.weathercast.data.DayForecast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastFragment extends Fragment {
	
	private DayForecast dayForecast;
	
	public ForecastFragment() {}
	
	public void setForecast(DayForecast dayForecast) {
		this.dayForecast = dayForecast;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//setContentView(R.layout.forecast_fragment);
		View v = inflater.inflate(R.layout.forecast_fragment, container, false);
		
		TextView tempView = (TextView) v.findViewById(R.id.temp);
		TextView descView = (TextView) v.findViewById(R.id.skydesc);
		tempView.setText( (int) (dayForecast.forecastTemp.min - 275.15) + "-" + (int) (dayForecast.forecastTemp.max - 275.15) );
		descView.setText(dayForecast.weather.currentCondition.getDescr());
		
		return v;
	}

}
