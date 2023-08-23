package com.example.proyectosemestral;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Traduccion traduccion;
    private FirebaseFirestore db;
    private ProgressBar spinner;
    Spinner sourceSpinner;
    Spinner targetSpinner;
    Button translateBtn;
    Button saveBtn;
    MediaPlayer mediaPlayer;
    Map<String, String> languages = new HashMap<String, String>() {{
        put("Español", "spanish");
        put("Francés", "french");
        put("Inglés", "english");
        put("Alemán","german");
        put("Portugués","portuguese");
    }};

    TextView outputText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (!tieneConexion(null)) finish();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        AgregarTraducciones traduccionesManager = new AgregarTraducciones(FirebaseFirestore.getInstance());
        translateBtn = findViewById(R.id.translate_button);
        saveBtn = findViewById(R.id.save_button);
        Button menuBtn = (Button) findViewById(R.id.back_to_menu_button);
        sourceSpinner = findViewById(R.id.source_language_spinner);
        targetSpinner = findViewById(R.id.output_language_spinner);
        TextView inputText = findViewById(R.id.input_text);
        this.outputText = findViewById(R.id.translated_text);
        mediaPlayer = MediaPlayer.create(this, R.raw.boton);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        List<String> languageKeys = new ArrayList<>(languages.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languageKeys);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(adapter);
        targetSpinner.setAdapter(adapter);
        sourceSpinner.setSelection(3);
        targetSpinner.setSelection(2);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tieneConexion(menuBtn)) return;
                Intent navegacion = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(navegacion);
            }

        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tieneConexion(translateBtn)) return;
                if(sourceSpinner.getSelectedItem().equals(targetSpinner.getSelectedItem())){
                    Toast.makeText(MainActivity.this, "Por favor, seleccione idiomas diferentes", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Ingrese el texto a traducir:");

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = input.getText().toString();
                        inputText.setText(text);
                        if (text == null || text.length() == 0) {
                            Toast.makeText(MainActivity.this, "No hay texto por traducir", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        spinner.setVisibility(View.VISIBLE);
                        String sourceLanguage = languages.get(sourceSpinner.getSelectedItem().toString());
                        String targetLanguage = languages.get(targetSpinner.getSelectedItem().toString());
                        translateBtn.setEnabled(false);
                        mediaPlayer.seekTo(0);
                        translateText(sourceLanguage, targetLanguage, text);
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            private Handler handler = new Handler();
            @Override
            public void onClick(View view) {
                if(!tieneConexion(saveBtn)) return;
                if (traduccion == null) {
                    Toast.makeText(MainActivity.this, "No hay traducción por guardar o ya guardó su traducción actual", Toast.LENGTH_SHORT).show();
                    saveBtn.setEnabled(false);
                    Handler timer = new Handler();
                    timer.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    saveBtn.setEnabled(true);
                                }
                            }, 2000
                    );
                    return;
                }
                saveBtn.setEnabled(false);
                traduccionesManager.agregarTraduccion(MainActivity.this,saveBtn, spinner,traduccion.getSource(), traduccion.getTarget(), traduccion.getText(), traduccion.getTranslation());
                traduccion = null;
            }
        });
    }

    private void translateText(String source, String target, String text) {
        String endpoint = "https://deep-translator-api.azurewebsites.net/google/";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                endpoint,
                createRequestPayload(source, target, text),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String translationResult = response.getString("translation");
                            String error = response.getString("error");
                            if (!error.equals("null")) throw new JSONException("Error en respuesta");
                            outputText.setText(translationResult);
                            traduccion = new Traduccion(
                                    sourceSpinner.getSelectedItem().toString(),
                                    targetSpinner.getSelectedItem().toString(),
                                    text,
                                    translationResult
                            );
                            mediaPlayer.start();
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Error en el manejo de JSON", Toast.LENGTH_SHORT).show();
                        } finally {
                            translateBtn.setEnabled(true);
                            spinner.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error en el servicio web", Toast.LENGTH_SHORT).show();
                        translateBtn.setEnabled(true);
                        spinner.setVisibility(View.GONE);
                    }
                }
        );

        requestQueue.add(request);


    }

    private JSONObject createRequestPayload(String source, String target, String text) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("source", source);
            payload.put("target", target);
            payload.put("text", text);
            payload.put("proxies", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload;
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
            Toast.makeText(MainActivity.this, "Por favor, verifique su conexión a Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
