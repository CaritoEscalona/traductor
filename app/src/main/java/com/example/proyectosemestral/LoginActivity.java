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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (!tieneConexion(null)) finish();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        Button registrarse=(Button) findViewById(R.id.button_registrate);
        login = (Button) findViewById(R.id.button_login);
        login.setText("Login");
        EditText emailInput = (EditText) findViewById(R.id.edit_text_email);
        EditText passwordInput = (EditText) findViewById(R.id.edit_text_password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                login.setEnabled(false);
                if (!tieneConexion(login)) return;
                iniciarSesion(email, password);

            }
        });
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tieneConexion(registrarse)) return;
                Intent navegacion = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(navegacion);
            }
        });
    }


    public void iniciarSesion(String correo, String clave) {

        if(correo.isEmpty()||clave.isEmpty()){
            Toast.makeText(LoginActivity.this, "Por favor ingrese todas las credenciales", Toast.LENGTH_SHORT).show();
            Button login = (Button) findViewById(R.id.button_login);
            login.setEnabled(true);
            return;
        }
        mAuth.signInWithEmailAndPassword(correo, clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();
                    startActivity(new Intent(LoginActivity.this,MenuActivity.class));
                    Toast.makeText(LoginActivity.this, "Bienvenido!", Toast.LENGTH_SHORT).show();
                    login.setEnabled(true);
                }else{
                    Toast.makeText(LoginActivity.this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                    login.setEnabled(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(tieneConexion(null)){
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();
            }
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
            Toast.makeText(LoginActivity.this, "Por favor, verifique su conexión a Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}