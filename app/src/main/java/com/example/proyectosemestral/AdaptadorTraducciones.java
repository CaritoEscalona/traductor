package com.example.proyectosemestral;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorTraducciones extends BaseAdapter {
    private List<Pair<Traduccion, String>> listaTraducciones;
    private Context context;
    private FirebaseFirestore db;
    public AdaptadorTraducciones(List<Pair<Traduccion, String>> listaTraducciones, Context context, FirebaseFirestore db) {
        this.listaTraducciones = listaTraducciones;
        this.context = context;
        this.db = db;
    }

    @Override
    public int getCount() {
        return listaTraducciones.size();
    }

    @Override
    public Pair<Traduccion, String> getItem(int position) {
        return listaTraducciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.adaptador, parent, false);
        }

        Pair<Traduccion, String> traduccionPair = getItem(position);
        Traduccion traduccion = traduccionPair.first;
        String traduccionId = traduccionPair.second;

        TextView sourceTextView = convertView.findViewById(R.id.source_text);
        TextView targetTextView = convertView.findViewById(R.id.target_text);
        TextView originalTextView = convertView.findViewById(R.id.original_text);
        TextView translationTextView = convertView.findViewById(R.id.translation_text);

        sourceTextView.setText(traduccion.getSource());
        targetTextView.setText(traduccion.getTarget());
        originalTextView.setText(traduccion.getText());
        translationTextView.setText(traduccion.getTranslation());

        Button eliminarButton = convertView.findViewById(R.id.delete_button);
        eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pair<Traduccion, String> traduccionPair = getItem(position);
                String traduccionId = traduccionPair.second;

                EliminarTraducciones eliminarTraducciones = new EliminarTraducciones(db);
                eliminarTraducciones.eliminarTraduccion(traduccionId);

                // Actualizar la vista del listado de perfil después de eliminar
                listaTraducciones.remove(traduccionPair);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
