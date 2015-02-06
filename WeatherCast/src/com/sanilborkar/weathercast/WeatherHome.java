package com.sanilborkar.weathercast;

import org.json.JSONException;

import com.sanilborkar.weathercast.adapter.ForecastAdapter;
import com.sanilborkar.weathercast.data.ForecastData;
import com.sanilborkar.weathercast.ForecastFragment;
import com.sanilborkar.weathercast.data.Weather;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;



public class WeatherHome extends FragmentActivity implements LocationListener {

	public static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
	private static final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&q=";
	
	private static final boolean CURRENT_LOC = true;
	private static final boolean CUSTOM_LOC = false;
	
	
	static LocationManager lm;
	static double lat, lon;
	static boolean isFahrenheit = true;
	
	//  -------------------------------------------
	
	private TextView cityText;
	private TextView coords;
	private TextView condDescr;
	private TextView temp;
	private TextView minTemp;
	private TextView maxTemp;
	private TextView press;
	private TextView windSpeed;
	private TextView windDeg;
	private TextView hum;
	private Button btnRefresh;
	private Button btnSearch;
	private EditText eCity;
	private Button btnC;
	private Button btnF;
	private ViewPager pager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_home);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//String city = "Gainesville,FL";
		
		cityText = (TextView) findViewById(R.id.cityText);
		coords = (TextView) findViewById(R.id.coords);
		condDescr = (TextView) findViewById(R.id.condDescr);
		temp = (TextView) findViewById(R.id.temp);
		minTemp = (TextView) findViewById(R.id.minTemp);
		maxTemp = (TextView) findViewById(R.id.maxTemp);
		hum = (TextView) findViewById(R.id.hum);
		press = (TextView) findViewById(R.id.press);
		windSpeed = (TextView) findViewById(R.id.windSpeed);
		windDeg = (TextView) findViewById(R.id.windDeg);
		
		btnRefresh = (Button) findViewById(R.id.btn_refresh);
		btnSearch = (Button) findViewById(R.id.btn_search);

		btnC = (Button) findViewById(R.id.btnC);
		btnF = (Button) findViewById(R.id.btnF);
		
		eCity = (EditText)findViewById(R.id.txtCityName);
		
		getInfo(CURRENT_LOC);
		
		btnRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            	String cityEntered = eCity.getText().toString();
            	if (cityEntered.matches("")) {
            		btnRefresh.setText("Refresh Current Location");
            		getInfo(CURRENT_LOC);
            	}
            	else {
            		btnRefresh.setText("Refresh " + cityEntered);
            		getInfo(CUSTOM_LOC);
            	}
            		
            }
        });
		
		
		btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            	String cityEntered = eCity.getText().toString();
            	btnRefresh.setText("Refresh " + cityEntered);
            	JSONWeatherTask task = new JSONWeatherTask(cityEntered);
        		task.execute();
            }
        });
		
		btnC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            	
            	int i = temp.getText().toString().indexOf(' ');
            	String tmp = temp.getText().toString().substring(0, i);
            	
            	i = minTemp.getText().toString().indexOf(' ');
            	String minTmp = temp.getText().toString().substring(0, i);
            	
            	i = maxTemp.getText().toString().indexOf(' ');
            	String maxTmp = temp.getText().toString().substring(0, i);
            	
            	// Convert F to C
            	if (isFahrenheit) {
            		temp.setText("" + Math.round((Float.parseFloat(tmp) -32)/(1.8f)) + " deg. C");
        			minTemp.setText("" + Math.round((Float.parseFloat(minTmp) -32)/(1.8f)) + " deg. C");
        			maxTemp.setText("" + Math.round((Float.parseFloat(maxTmp) -32)/(1.8f)) + " deg. C");
            	}
            	
            	isFahrenheit = false;
            	
            }
        });
		
		btnF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            	
            	int i = temp.getText().toString().indexOf(' ');
            	String tmp = temp.getText().toString().substring(0, i);
            	
            	i = minTemp.getText().toString().indexOf(' ');
            	String minTmp = temp.getText().toString().substring(0, i);
            	
            	i = maxTemp.getText().toString().indexOf(' ');
            	String maxTmp = temp.getText().toString().substring(0, i);
            	
            	// Convert C to F
            	if (!isFahrenheit) {
            		
            		temp.setText("" + Math.round((Float.parseFloat(tmp)*(1.8f) + 32)) + " deg. F");
        			minTemp.setText("" + Math.round((Float.parseFloat(minTmp)*(1.8f) + 32)) + " deg. F");
        			maxTemp.setText("" + Math.round((Float.parseFloat(maxTmp)*(1.8f) + 32)) + " deg. F");
            	}

            	isFahrenheit = true;
            	
            }
        });
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather_home, menu);
        return true;
    }
	
	
    // Start getting the weather info
    public void getInfo(boolean mode) {
    	JSONWeatherTask w_task; 
    	JSONForecastWeatherTask fc_task;
    	String forecastDays = "3";
    	
    	if (mode) {
			getCurrentLocation();
			w_task = new JSONWeatherTask(lat, lon);
			fc_task = new JSONForecastWeatherTask(lat, lon);
    	}
    	else {
    		eCity   = (EditText)findViewById(R.id.txtCityName);           	
        	w_task = new JSONWeatherTask(eCity.getText().toString());
        	fc_task = new JSONForecastWeatherTask(eCity.getText().toString(), forecastDays);
    	}

		w_task.execute();
		//fc_task.execute();
    }
    
    
	private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
		
		private String RequestURL;
		
		public JSONWeatherTask(String city) {
			super();
			this.setRequestURL(WEATHER_URL + "?q=" + city);
		}
		
		public JSONWeatherTask(double lat, double lon) {
			super();
			this.setRequestURL(WEATHER_URL + "?lat="+lat+"&lon="+lon);
		}
		
		@Override
		protected Weather doInBackground(String... params) {
			Weather weather = new Weather();
			String data = ( (new FormatRequest()).getWeatherData(getRequestURL()));

			try {
				weather = ProcessResponse.extractWeatherInfo(data);
				
				
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			return weather;
		
	}
		
		
		
		
	@Override
		protected void onPostExecute(Weather weather) {			
			super.onPostExecute(weather);
			
			cityText.setText(weather.location.getCity() + ", " + weather.location.getCountry());
			coords.setText("(" + weather.location.getLatitude() + ", " + weather.location.getLongitude() + ")");
			condDescr.setText(weather.currentCondition.getCondition() + " - " + weather.currentCondition.getDescr());
			
			
			displayTemp(weather.temperature.getTemp(), weather.temperature.getMinTemp(), weather.temperature.getMaxTemp());			
			
			hum.setText("" + weather.currentCondition.getHumidity() + "%");
			press.setText("" + weather.currentCondition.getPressure() + " hPa");
			windSpeed.setText("" + weather.wind.getSpeed() + " mps");
			windDeg.setText("Direction: " + weather.wind.getDeg() + " deg.");
				
		}

	public String getRequestURL() {
		return RequestURL;
	}

	public void setRequestURL(String requestURL) {
		RequestURL = requestURL;
	}






	
  }

	
private class JSONForecastWeatherTask extends AsyncTask<String, Void, ForecastData> {
		
	private String RequestURL;
	private int forecastDaysNum;
	
	public JSONForecastWeatherTask(String city, String forecastDays) {
		super();
		this.setRequestURL(FORECAST_URL + "?q=" + city);
		this.forecastDaysNum = Integer.parseInt(forecastDays);
	}
	
	public JSONForecastWeatherTask(double lat, double lon) {
		super();
		this.setRequestURL(FORECAST_URL + "?lat="+lat+"&lon="+lon);
	}
	
		protected ForecastData doInBackground(String... params) {
			
			String data = ( (new FormatRequest()).getWeatherForecastData(params[0]));
			ForecastData forecast = new ForecastData();
			try {
				forecast = ProcessResponse.extractWeatherForecast(data);
				System.out.println("Weather ["+forecast+"]");
				
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			return forecast;
		
	}
		
		
		
		
	@Override
		protected void onPostExecute(ForecastData forecastData) {			
			super.onPostExecute(forecastData);
            		
			ForecastAdapter adapter = new ForecastAdapter(forecastDaysNum, getSupportFragmentManager(), forecastData);
			pager.setAdapter(adapter);
		}

	public String getRequestURL() {
		return RequestURL;
	}

	public void setRequestURL(String requestURL) {
		RequestURL = requestURL;
	}



  }
	
	
	//  ------------------------------------------
	
	
	public void displayTemp(float tmp, float minTmp, float maxTmp)
	{
		if (isFahrenheit) {
			temp.setText("" + Math.round((tmp - 273.15)*(1.8f) + 32) + " deg. F");
			minTemp.setText("" + Math.round((minTmp - 273.15)*(1.8f) + 32) + " deg. F");
			maxTemp.setText("" + Math.round((maxTmp - 273.15)*(1.8f) + 32) + " deg. F");
		}
		else {
			temp.setText("" + Math.round((tmp - 273.15f)) + " deg. C");
			minTemp.setText("" + Math.round((minTmp - 273.15f)) + " deg. C");
			maxTemp.setText("" + Math.round((maxTmp - 273.15f)) + " deg. C");
			//isFahrenheit = false;
		}
		
	}



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    /*
     * GPS LOCATION LISTENER
     * removeUpdates CALLED ON BOTH THE LOCATION LISTENERS
     */
    public static LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            //timer.cancel();
            //double x =location.getLatitude();
            //double y = location.getLongitude();
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }

        public void onProviderDisabled(String provider) { }

        public void onProviderEnabled(String provider) { }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
    };

    /*
     * NETWORK LOCATION LISTENER
     * removeUpdates CALLED ON BOTH THE LOCATION LISTENERS
     */
    public static LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            //timer.cancel();
            //double x = location.getLatitude();
            //double y = location.getLongitude();
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);

        }

        public void onProviderDisabled(String provider) { }

        public void onProviderEnabled(String provider) { }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
    };

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
    
    
	 /*
	    *	LOCATION RELATED SUB-ROUTINES
	    *	GET THE TIMESTAMP OF THE LOCATIONS ACQUIRED BY BOTH THE GPS AND NETWORK PROVIDERS. USE THE LATEST ONE.
	    *	IF GPS IS NOT PRESENT, USE THE NETWORK PROVIDER
	    *	IF NETWORK IS NOT PRESENT, USE THE GPS PROVIDER
	    */
	    private void getCurrentLocation() {
	    	
	    	boolean gps_enabled = false;
	    	boolean network_enabled = false; 
	    	
	    	// LOCATION STUFF	    	
	        	if (lm != null)
	        	{
	        		gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    	        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	        		//lm.removeUpdates(HomeFragment.locationListenerGps);
	        		//lm.removeUpdates(HomeFragment.locationListenerNetwork);
	        	}

	            Location net_loc=null, gps_loc=null;
	            if(gps_enabled)
	                gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	            if(network_enabled)
	                net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

	            //if there are both values use the latest one
	            if(gps_loc!=null && net_loc!=null){
	                if(gps_loc.getTime()>net_loc.getTime())
	               {
	                	lat = gps_loc.getLatitude();
	                	lon = gps_loc.getLongitude();
	               }
	                else
	               {
	                	lat = net_loc.getLatitude();
	                	lon = net_loc.getLongitude();
	               }

	            }
	            else if(gps_loc!=null){
	                 {
	                	 lat = gps_loc.getLatitude();
	                	 lon = gps_loc.getLongitude();
	                 }

	            }
	            else if(net_loc!=null)
	               {
	            	lat = net_loc.getLatitude();
	            	lon = net_loc.getLongitude();
	            }
	            else
	            {
	            	// No Provider
	            }
	        
	        }
	
}
