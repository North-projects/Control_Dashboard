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
import android.widget.EditText;
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


public class Temperature extends AppCompatActivity  {

    private static final String TAG = Temperature.class.getSimpleName();

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
        setContentView(R.layout.activity_temperature);
        textPHP = findViewById(R.id.textPHP);
        handler.postDelayed(sendData, REFRESH_DELAY);

        final EditText tempnum = findViewById(R.id.tempnum);

        //Nastavitev akcije za gumb
        Button ogrevanje = findViewById(R.id.ogrevanje);
        Button mtempset = findViewById(R.id.Tempset);


        ogrevanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Temperature.this, "Signal poslan", Toast.LENGTH_SHORT).show();
            }
        });

        //Nastavitev pošiljanja ukaza ob pritisku gumba ogrevanje
        ogrevanje.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    /* button je ročno ogreavnje */
                    new Background_get().execute("ogrevanje=1");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    new Background_get().execute("ogrevanje=0");
                }
                return false;
            }
        });


        //Nastavitev gumba za regulacijo
        mtempset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((tempnum.getText().toString().length() == 0) | (tempnum.getText().toString().length() > 2)){
                    tempnum.setText("0");
                }

                int temp1 = Integer.parseInt(tempnum.getText().toString());
                String temp = String.valueOf(tempnum);

                if (temp1 > 0 & temp1 < 30 ){
                    Toast.makeText(Temperature.this, "Številka poslana", Toast.LENGTH_SHORT).show();
                    new Background_get().execute(temp);
                }
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

    // CONNECTION_TIMEOUT in READ_TIMEOUT podatka sta podana v  milisekundah
    public static final int CONNECTION_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 6000;
    TextView textPHP;

    private class AsyncRetrieve extends AsyncTask<String, String, String> {
                ProgressDialog pdLoading = new ProgressDialog(Temperature.this);
                HttpURLConnection conn;
                URL url = null;

        // Izvaja se v ozadju, rezultat gre na onPostExecute za prikaz
        @Override
        protected String doInBackground(String... params) {
            try {
                // URL naslov od HAS Pi z imenom php datoteke
                url = new URL("http://192.168.43.242/temp.php");

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
            if (result.equals("Ni možno pridobiti podatka")) {
                textPHP.setText(result);
            } else {
                textPHP.setText(result);
            }
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendData);
        super.onDestroy();
    }
}