package com.example.proyectosemestral;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AgregarTraducciones {
    private FirebaseFirestore db;
    FirebaseAuth mAuth;
    public AgregarTraducciones(FirebaseFirestore firestore) {
        db = firestore;
        mAuth = FirebaseAuth.getInstance();
    }

    public void agregarTraduccion(Context activity, Button btn, ProgressBar spinner, String source, String target, String textoOriginal, String textoTraducido) {
        String usuario = mAuth.getCurrentUser().getEmail();
        Map<String, Object> traduccionMap = new HashMap<>();
        traduccionMap.put("usuario", usuario);
        traduccionMap.put("origen", source);
        traduccionMap.put("destino", target);
        traduccionMap.put("textoOriginal", textoOriginal);
        traduccionMap.put("textoTraducido", textoTraducido);
        spinner.setVisibility(View.VISIBLE);

        db.collection("traducciones")
                .add(traduccionMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {//exito al guardar la traducción
                        Toast.makeText(activity, "Guardado!", Toast.LENGTH_SHORT).show();
                        btn.setEnabled(true);
                        spinner.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {//error al guardar la traducción

                        Toast.makeText(activity, "Error al guardar.", Toast.LENGTH_SHORT).show();
                        btn.setEnabled(true);
                        spinner.setVisibility(View.GONE);
                    }
                });
    }
}

