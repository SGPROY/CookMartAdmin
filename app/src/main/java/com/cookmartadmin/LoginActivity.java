package com.cookmartadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText correo, contrasena;
    private ProgressDialog cuadroDeDialogoProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        correo = findViewById(R.id.login_correo);
        contrasena = findViewById(R.id.login_password);
        contrasena.setTransformationMethod(new PasswordTransformationMethod());

        Window ventana = this.getWindow();
        ventana.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ventana.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ventana.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        cuadroDeDialogoProgreso = new ProgressDialog(this);
        cuadroDeDialogoProgreso.setTitle("Por favor espere...");
        cuadroDeDialogoProgreso.setMessage("Mientras completamos su inicio de sesión.");
        cuadroDeDialogoProgreso.setCanceledOnTouchOutside(false);
    }

    public void abrirPantallaRegistro(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterScreen.class));
    }

    public void iniciarSesionUsuario(View view) {
        if (!correo.getText().toString().equals("") && !contrasena.getText().toString().equals("")) {
            cuadroDeDialogoProgreso.show();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(correo.getText().toString(), contrasena.getText().toString())
                    .addOnSuccessListener(authResult -> FirebaseDatabase.getInstance().getReference("admins").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                                        SplashScreen.sharedPreferences.edit().putString("nombre", hashMap.get("nombre")).apply();
                                        SplashScreen.sharedPreferences.edit().putString("correo", hashMap.get("correo")).apply();
                                        if (hashMap.get("perfil") != null) {
                                            SplashScreen.sharedPreferences.edit().putString("perfil", hashMap.get("perfil")).apply();
                                        }
                                        cuadroDeDialogoProgreso.cancel();
                                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    cuadroDeDialogoProgreso.cancel();
                                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    cuadroDeDialogoProgreso.cancel();
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No se puede procesar un campo en blanco.", Toast.LENGTH_SHORT).show();
        }
    }
}
