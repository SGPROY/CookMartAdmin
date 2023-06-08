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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class RegisterScreen extends AppCompatActivity {

    private EditText nombre, correo, contrasena;
    private ProgressDialog cuadroDeDialogoProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Window ventana = this.getWindow();
        ventana.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ventana.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ventana.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        cuadroDeDialogoProgreso = new ProgressDialog(this);
        cuadroDeDialogoProgreso.setTitle("Por favor espere...");
        cuadroDeDialogoProgreso.setMessage("Mientras completamos su registro.");
        cuadroDeDialogoProgreso.setCanceledOnTouchOutside(false);

        nombre = findViewById(R.id.nombre_registro);
        correo = findViewById(R.id.registrar_correo);
        contrasena = findViewById(R.id.registrar_contrasena);
        contrasena.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void abrirPantallaLoginDesdeRegistro(View view) {
        startActivity(new Intent(RegisterScreen.this, LoginActivity.class));
    }

    public void registrarUsuario(View view) {
        if (!correo.getText().toString().equals("") && !contrasena.getText().toString().equals("") && !nombre.getText().toString().equals("")) {
            cuadroDeDialogoProgreso.show();
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo.getText().toString(), contrasena.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            cuadroDeDialogoProgreso.show();
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("nombre", nombre.getText().toString());
                            hashMap.put("correo", correo.getText().toString());
                            FirebaseDatabase.getInstance().getReference("admins").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(hashMap).addOnSuccessListener(aVoid -> {
                                SplashScreen.sharedPreferences.edit().putString("nombre", hashMap.get("nombre")).apply();
                                SplashScreen.sharedPreferences.edit().putString("correo", hashMap.get("correo")).apply();
                                cuadroDeDialogoProgreso.cancel();
                                Toast.makeText(RegisterScreen.this, "Registrado exitosamente", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterScreen.this, MainActivity.class));
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    cuadroDeDialogoProgreso.cancel();
                                    Toast.makeText(RegisterScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    cuadroDeDialogoProgreso.cancel();
                    Toast.makeText(RegisterScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No se puede procesar un campo en blanco.", Toast.LENGTH_SHORT).show();
        }
    }


    // Función para convertir la contraseña en un hash MD5
    public String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
