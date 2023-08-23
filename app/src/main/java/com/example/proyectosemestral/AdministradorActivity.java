package com.example.proyectosemestral;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AdministradorActivity extends AppCompatActivity {

    private Button btnVerUsuarios;

    private TextView tvUsuarios;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (isConnected()) {
        } else {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        btnVerUsuarios = findViewById(R.id.btn_ver_usuarios);
        tvUsuarios = findViewById(R.id.tv_usuarios);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnVerUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tieneConexion()){
                    obtenerUsuarios();
                }
            }
        });


    }

    private void obtenerUsuarios(){
        db.collection("user").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder usuariosBuilder = new StringBuilder();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String email = document.getString("email");
                    usuariosBuilder.append(email).append("\n");
                }
                tvUsuarios.setText(usuariosBuilder.toString());
            } else {
                Toast.makeText(AdministradorActivity.this, "Error al obtener los usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean tieneConexion(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(AdministradorActivity.this, "Por favor, verifique su conexión a Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

