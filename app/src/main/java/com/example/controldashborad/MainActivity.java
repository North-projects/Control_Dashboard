package com.example.controldashborad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView ograja,gvrata,temp,light;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //definaranje kartic oziroma povezovanje id oznak
        ograja = (CardView) findViewById(R.id.ograja);
        gvrata = (CardView) findViewById(R.id.gvrata);
        temp = (CardView) findViewById(R.id.temp);
        light = (CardView) findViewById(R.id.light);

        // Doda Click listener na karte
        ograja.setOnClickListener(this);
        gvrata.setOnClickListener(this);
        temp.setOnClickListener(this);
        light.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i ;
        //Preveri katera kartica je bila izbrana ter zažene aktivnost
        switch (v.getId()) {
            case R.id.ograja:
                i = new Intent(this, Fence.class);
                startActivity(i);
                break;
            case R.id.gvrata:
                i = new Intent(this, Garage.class);
                startActivity(i);
                break;
            case R.id.temp:
                i = new Intent(this, Temperature.class);
                startActivity(i);
                break;
            case R.id.light:
                i = new Intent(this, Lights.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
