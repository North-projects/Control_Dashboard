package com.example.controldashborad;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;




public class Lights extends AppCompatActivity {

    private static final String TAG = Lights.class.getSimpleName();

    //Nastavitev ponovljive zanke
    private static final int REFRESH_DELAY = 3000;
    private final Handler handler = new Handler();
    private final Runnable sendData = new Runnable(){
        public void run(){
            try {
                new AsyncRetrieve().execute();
                Log.e(TAG, "Osvežujem");
                handler.postDelayed(sendData, REFRESH_DELAY);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);
        status1 = findViewById(R.id.status1);
        handler.postDelayed(sendData, REFRESH_DELAY);

        //Nastavitev akcije za gumb
        Button gumb_dol = findViewById(R.id.gumb_dol);
        Button gumb_gor = findViewById(R.id.gumb_gor);

        gumb_dol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Lights.this, "Signal poslan", Toast.LENGTH_SHORT).show();
            }
        });

        gumb_gor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Lights.this, "Signal poslan", Toast.LENGTH_SHORT).show();
            }
        });

        //Nastavitev pošiljanja ukaza ob pritisku gumba
        gumb_dol.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new Background_get().execute("spust=1");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    new Background_get().execute("spust=0");
                }
                return false;
            }
        });

        gumb_gor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new Background_get().execute("dvig=1");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    new Background_get().execute("dvig=0");
                }
                return false;
            }
        });

    }

    /* Povezava za Has Pi */

    private static class Background_get extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("http://192.168.43.242/?" + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");

                in.close();
                connection.disconnect();
                return result.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static final int CONNECTION_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 6000;
    TextView status1;

    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Lights.this);
        HttpURLConnection conn;
        URL url = null;

        // Izvaja se v ozadju, rezultat gre na onPostExecute za prikaz
        @Override
        protected String doInBackground(String... params) {
            try {
                // URL naslov od HAS Pi z imenom php datoteke
                url = new URL("http://192.168.43.242/lux.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Nastavitev HttpURLConnection za pošiljanje in pridobivanje podatkov iz index.php
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput je true ko dobimo podatke iz datoteke
                conn.setDoOutput(true);

            } catch (IOException e1) {
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Preveri če je povezava uspešna
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Prebere podatke poslane iz serverja
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Prenese podatke na onPostExecute
                    return (result.toString());

                } else {

                    return ("Neuspešna povezava");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        // Izvaja se v ozadju, rezultat gre na doInBackground
        @Override
        protected void onPostExecute(String result) {

            pdLoading.dismiss();
            if (result.equals("Vklopjeno")) {
                status1.setText(result.toString());
                status1.setTextColor(0xFF6FD25A);
            } else if (result.equals("Izklopljeno")) {
                status1.setText(result.toString());
                status1.setTextColor(0xFFFF0000);
            } else {
                // Za razumevanje napake vrnjene iz doInBackground
                Toast.makeText(Lights.this, result.toString(), Toast.LENGTH_SHORT).show();
            }
        }
}

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendData);
        super.onDestroy();
    }
}

