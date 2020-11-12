package com.example.ligneactivite.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.ligneactivite.Activity.MainActivity;
import com.example.ligneactivite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Contact extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        //Navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation_contact);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_accueil:
                        Intent otherActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(otherActivity);
                        finish();
                        break;
                    case R.id.action_logo:

                        clicked_button("https://www.google.fr/maps");
                        break;
                    case R.id.action_landscape:
                        clicked_button("http://www.google.fr");
                        break;                }
                return true;
            }
        });





    }
    public void clicked_button(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}