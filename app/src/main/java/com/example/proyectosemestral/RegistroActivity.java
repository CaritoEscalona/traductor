package com.example.proyectosemestral;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {
    Button btn_Registro;
    EditText name, email, password,passwordConfirm;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (!tieneConexion(null)) finish();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        // Inicializar FirebaseApp
        FirebaseApp.initializeApp(this);
        this.setTitle("Registro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.edit_text_email);
        password = (EditText) findViewById(R.id.edit_text_password);
        passwordConfirm = (EditText) findViewById(R.id.edit_text_password_confirm);
        btn_Registro = (Button) findViewById(R.id.button_registro);

        btn_Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailUser = email.getText().toString().trim();
                String passUser = password.getText().toString().trim();
                String passUserConfirm = passwordConfirm.getText().toString().trim();
                if (!tieneConexion(btn_Registro)) return;

                if (emailUser.isEmpty() || passUser.isEmpty() || passUserConfirm.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
                    btn_Registro.setEnabled(false);
                    Handler timer = new Handler();
                    timer.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                btn_Registro.setEnabled(true);
                            }
                        }, 2000
                    );
                } else if (!passUserConfirm.equals(passUser)) {
                    Toast.makeText(RegistroActivity.this, "Las contraseña no coinciden", Toast.LENGTH_SHORT).show();
                    btn_Registro.setEnabled(false);
                    Handler timer = new Handler();
                    timer.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                btn_Registro.setEnabled(true);
                            }
                        }, 2000
                    );
                } else if(passUser.length()<6) {
                    Toast.makeText(RegistroActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    btn_Registro.setEnabled(false);
                    Handler timer = new Handler();
                    timer.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                btn_Registro.setEnabled(true);
                            }
                        }, 2000
                    );
                }else {
                    btn_Registro.setEnabled(false);
                    RegistroUser(emailUser, passUser);
                }
            }
        });
    }

    private void RegistroUser(String emailUser, String passUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                String id = mAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("email", emailUser);

                mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(RegistroActivity.this, MenuActivity.class));
                        Toast.makeText(RegistroActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                        btn_Registro.setEnabled(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(RegistroActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                        btn_Registro.setEnabled(true);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(RegistroActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
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
            Toast.makeText(RegistroActivity.this, "Por favor, verifique su conexión a Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}