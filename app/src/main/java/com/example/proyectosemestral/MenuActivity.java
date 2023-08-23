package com.example.proyectosemestral;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (!tieneConexion(null )) finishAndRemoveTask();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button traducir = (Button) findViewById(R.id.traducir);
        Button perfil = (Button) findViewById(R.id.perfil);
        Button salir = (Button) findViewById(R.id.salir);
        ImageButton audio =(ImageButton) findViewById(R.id.admin);
        mediaPlayer = MediaPlayer.create(this, R.raw.instruccion);


        traducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tieneConexion(traducir )) return;
                Intent navegacion = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(navegacion);
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tieneConexion(perfil )) return;
                Intent navegacion = new Intent(MenuActivity.this, Perfil.class);
                startActivity(navegacion);
            }
        });
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!tieneConexion(salir )) return;
                    // Cerrar sesión
                FirebaseAuth.getInstance().signOut();
                Intent navegacion = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(navegacion);
                finish();
                Toast.makeText(MenuActivity.this, "Se cerró la sesión exitosamente", Toast.LENGTH_SHORT).show();

            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audio.setEnabled(false);
                mediaPlayer.seekTo(0);
                Handler timer = new Handler();
                timer.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            audio.setEnabled(true);
                        }
                    }, 15000
                );
                mediaPlayer.start();

            }
        });
/*
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tieneConexion(audio )) return;
                Intent navegacion = new Intent(MenuActivity.this, AdministradorActivity.class);
                startActivity(navegacion);
            }
        });
        */
    }
    private boolean tieneConexion(Button btn) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            if(btn!=null) {
                btn.setEnabled(false);
                Handler timer = new Handler();
                timer.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            btn.setEnabled(true);
                        }
                    }, 2000
                );
            }
            Toast.makeText(MenuActivity.this, "Por favor, verifique su conexión a Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}