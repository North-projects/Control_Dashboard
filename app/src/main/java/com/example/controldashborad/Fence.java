package com.example.controldashborad;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Fence extends AppCompatActivity {
    @SuppressLint("ClickableViewAccessibility")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence);

        //Nastavitev akcije za gumb
        Button ograja = findViewById(R.id.ograja);

        ograja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Fence.this, "Signal poslan", Toast.LENGTH_SHORT).show();
            }
        });

        //Nastavitev pošiljanja ukaza ob pritisku gumba
        ograja.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    /* button is ograja */
                    new Background_get().execute("ograja=1");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    new Background_get().execute("ograja=0");
                }
                return false;
            }
        });
    }

    /* Povezava za HAS Pi */

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
}