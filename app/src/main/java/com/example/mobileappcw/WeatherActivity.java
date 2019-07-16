package com.example.mobileappcw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WeatherActivity  extends AppCompatActivity implements SensorEventListener {
    //Firebase
    private FirebaseAuth firebaseAuth;
    // Accelerometer
    private SensorManager sensorManager;
    private Sensor sensor;
    private long lastUpdateTime;
    private static float SHAKE_THRESHOLD_GRAVITY = 2;
    // Open Weather API
    private static final String OPEN_WEATHER_MAP_API_KEY = "f6cde51da66cfa2298febfebcfce3c3b";
    private static final String units = "metric";
    TextView textView_temperature;
    TextView textView_pressure;
    TextView textView_description;
    TextView textView_wind;
    TextView textView_humidity;
    ImageView iconImage;
    weatherModel WeatherModel = new weatherModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Sensor management
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdateTime = System.currentTimeMillis();

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        //Find and set views
        iconImage = (ImageView) findViewById(R.id.imageViewIcon);
        iconImage.setColorFilter(Color.rgb(251, 188, 5));
        textView_temperature = (TextView) findViewById(R.id.textView_temperature);
        textView_pressure = (TextView) findViewById(R.id.textView_pressure);
        textView_description = (TextView) findViewById(R.id.textView_description);
        textView_wind = (TextView) findViewById(R.id.textView_wind);
        textView_humidity = (TextView) findViewById(R.id.textView_humidity);
    }

    public void searchWeather(View view){
        hideKeyboard();
        EditText searchText = (EditText) findViewById(R.id.txt_User_Search);
        String searchParams = searchText.getText().toString();
        String url = String.format(
                "http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=%s&units=%s",
                searchParams, OPEN_WEATHER_MAP_API_KEY, units
        );

        new GetWeatherTask().execute(url);
    }

    public void logOut(View view) {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(WeatherActivity.this, LoginActivity.class));
    }

    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class GetWeatherTask extends AsyncTask<String, Void, weatherModel> {

        @Override
        protected weatherModel doInBackground(String... strings)
        {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode() != 200) {
                    GetWeatherTask.this.cancel(true);
                }
                    InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder builder = new StringBuilder();

                    String inputString;
                    while ((inputString = bufferedReader.readLine()) != null){
                        builder.append(inputString);
                    }

                    JSONObject topLevel = new JSONObject(builder.toString());
                    JSONObject main = topLevel.getJSONObject("main");
                    JSONObject wind = topLevel.getJSONObject("wind");
                    WeatherModel.setTemp(String.valueOf(main.getDouble("temp")));
                    WeatherModel.setPressure(String.valueOf(main.getDouble("pressure")));

                    // Get description from Array
                    JSONArray weatherArray = topLevel.getJSONArray("weather");
                    for(int i=0;i<weatherArray.length();i++) {
                        JSONObject e = weatherArray.getJSONObject(i);
                        WeatherModel.setDescription(String.valueOf(e.getString("description")));
                    }

                    WeatherModel.description = WeatherModel.description.substring(0,1).toUpperCase()
                            + WeatherModel.description.substring(1).toLowerCase();
                    WeatherModel.setHumidity(String.valueOf(main.getInt("humidity")));
                    WeatherModel.setSpeed(String.valueOf(wind.getDouble("speed")));


                    urlConnection.disconnect();
                    stream.close();
            } catch (JSONException | IOException e){
                e.printStackTrace();
            }
            return WeatherModel;
        }

        @Override
        protected void onPostExecute(weatherModel weather) {
                textView_temperature.setText("Temperature: " + weather.temp + "°C");
                textView_pressure.setText("Current pressure: " + weather.pressure);
                textView_description.setText("Description: " + weather.description + ".");
                textView_humidity.setText("Humidity: " + weather.humidity + "%");
                textView_wind.setText("Wind: " + weather.speed + "MPH");
        }
    }

    private InputStream OpenHTTPConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL weatherURL = new URL(urlString);
        URLConnection conn = weatherURL.openConnection();

        if (! (conn instanceof HttpURLConnection) )
            throw new IOException("Not HTTP Connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
                }
            }
            catch (Exception ex) {
                Log.d("Networking", ex.getLocalizedMessage());
                throw new IOException("Error connecting");
            }
            return in;
        }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        float gForce = (float)Math.sqrt(gX * gY * gY + gZ * gZ);

        long currentTime = System.currentTimeMillis();
        if(gForce >= SHAKE_THRESHOLD_GRAVITY)
        {
            if (currentTime - lastUpdateTime < 200) {
                return;
            }
            lastUpdateTime = currentTime;
            startActivity(new Intent(WeatherActivity.this, StackActivity.class));
        }
    }
}
