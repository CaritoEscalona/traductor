package com.example.proyectosemestral;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class EliminarTraducciones {
    private FirebaseFirestore db;

    public EliminarTraducciones(FirebaseFirestore firestore) {
        db = firestore;
    }

    public void eliminarTraduccion(String traduccionId) {
        db.collection("traducciones").document(traduccionId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La traducción se eliminó correctamente
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar la traducción
                    }
                });
    }

}
