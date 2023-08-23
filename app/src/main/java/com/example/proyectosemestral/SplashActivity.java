package com.example.proyectosemestral;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {

      if (tieneConexion()) {
      } else {
          new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                  finish();
              }
          }, 2000);
      }

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    Handler timer = new Handler();
    timer.postDelayed(
        new Runnable() {
          @Override
          public void run() {
            Intent navegation = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(navegation);
            finish();
          }
        }, 2000
    );
  }
    private boolean tieneConexion(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(SplashActivity.this, "Por favor, verifique su conexión a Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}