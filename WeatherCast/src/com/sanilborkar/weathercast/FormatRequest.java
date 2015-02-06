package com.sanilborkar.weathercast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FormatRequest {
		
	private static final String LANG = "en";
	
		public String getWeatherData(String RequestURL) {
			HttpURLConnection con = null ;
			InputStream is = null;

			try {
				con = (HttpURLConnection) ( new URL(RequestURL)).openConnection();
				con.setRequestMethod("GET");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.connect();
				
				// Read the response
				StringBuffer buffer = new StringBuffer();
				is = con.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while (  (line = br.readLine()) != null )
					buffer.append(line + "\r\n");
				
				is.close();
				con.disconnect();
				return buffer.toString();
		    }
			catch(Throwable t) {
				t.printStackTrace();
			}
			finally {
				try { is.close(); } catch(Throwable t) {}
				try { con.disconnect(); } catch(Throwable t) {}
			}

			return null;
		}
		
		
		public String getWeatherForecastData(String forecastDays) {
			HttpURLConnection con = null ;
			InputStream is = null;
			
			int forecastDaysNum = Integer.parseInt(forecastDays);
					
			try {
					
				String url = "";
				url = url + "&lang=" + LANG;
				
				url = url + "&cnt=" + forecastDaysNum;
				con = (HttpURLConnection) ( new URL(url)).openConnection();
				con.setRequestMethod("GET");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.connect();
				
				// Let's read the response
				StringBuffer buffer1 = new StringBuffer();
				is = con.getInputStream();
				BufferedReader br1 = new BufferedReader(new InputStreamReader(is));
				String line1 = null;
				while (  (line1 = br1.readLine()) != null )
					buffer1.append(line1 + "\r\n");
				
				is.close();
				con.disconnect();
				
				System.out.println("Buffer ["+buffer1.toString()+"]");
				return buffer1.toString();
		    }
			catch(Throwable t) {
				t.printStackTrace();
			}
			finally {
				try { is.close(); } catch(Throwable t) {}
				try { con.disconnect(); } catch(Throwable t) {}
			}

			return null;
					
		}


}
