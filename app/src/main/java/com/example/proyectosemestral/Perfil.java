package com.example.proyectosemestral;

import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Perfil extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListView listView;
    private ProgressBar spinner;
    private AdaptadorTraducciones adapter;
    private EliminarTraducciones eliminarTraducciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (!tieneConexion(null)) finish();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        listView = findViewById(R.id.listView);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        Button menu = findViewById(R.id.menu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tieneConexion(menu)) return;
                Intent navegacion = new Intent(Perfil.this, MenuActivity.class);
                startActivity(navegacion);
            }
        });

        // Consulta los documentos de las traducciones para el usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            db.collection("traducciones").whereEqualTo("usuario", userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    ArrayList<Pair<Traduccion, String>> traducciones = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Obtén los datos de cada documento y crea objetos Traduccion
                        String traduccionId = document.getId();
                        String source = document.getString("origen");
                        String target = document.getString("destino");
                        String text = document.getString("textoOriginal");
                        String translation = document.getString("textoTraducido");

                        Traduccion traduccion = new Traduccion(source, target, text, translation);

                        Pair<Traduccion, String> traduccionPair = new Pair<>(traduccion, traduccionId);
                        traducciones.add(traduccionPair);
                    }

                    // Utiliza la variable de instancia "adapter" en lugar de crear una nueva variable local
                    adapter = new AdaptadorTraducciones(traducciones, Perfil.this, db);
                    listView.setAdapter(adapter);
                    spinner.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Perfil.this, "Error al cargar traducciones", Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                    // Manejo de errores
                }
            });
        }
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
            Toast.makeText(Perfil.this, "Por favor, verifique su conexión a Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
