package com.caitlynwiley.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView weatherText;
    EditText cityText;
    final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    final String EXTRAS = "&units=imperial&APPID=078a58c0462312f3c2f72810e18a4a15";
    int zipCode;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherText = findViewById(R.id.weatherText);
        cityText = findViewById(R.id.cityName);
    }

    public void getCity(View view) {
        // hide keyboard
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityText.getWindowToken(), 0);

        //get text, remove spaces on beginning/end of string, and encode the internal spacess manually
        city = cityText.getText().toString().trim().replace(" ", "%20");

        if (city.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter a city name.", Toast.LENGTH_SHORT).show();
        }

        DownloadWeather task = new DownloadWeather();
        task.execute(BASE_URL + city + EXTRAS);
    }

    public class DownloadWeather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                int data;
                while ((data = reader.read()) != -1) {
                    result += (char) data;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("")) {
                Toast.makeText(getApplicationContext(), "Invalid city name.", Toast.LENGTH_SHORT).show();
            }

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                String weather = "";
                for (int i = 0; i < weatherArray.length(); i++) {
                    String main = weatherArray.getJSONObject(i).getString("main");
                    String description = weatherArray.getJSONObject(i).getString("description");
                    weather += main + ": " + description + "\n";
                }
                weatherText.setText(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
