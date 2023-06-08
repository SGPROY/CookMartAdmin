package com.cookmartadmin;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.cookmartadmin.models.modeloProductos;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class AddProducto extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final String[] spinnerUnitArr = new String[]{"Kilogramos", "Litros", "Unidades"};
    private EditText titulo, descripcion, precio;
    private ImageView imgMenor, imgMayor;
    private TextView unidad;
    private String categoria;
    private String idProducto;
    private boolean imgMenorCambia, imgMayorCambia, seVendio;
    private String urlMenor, urlMayor;
    private ProgressDialog progressDialog;
    private Uri uriMenor, uriMayor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_producto);
        categoria = getIntent().getStringExtra("categoria");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor..");
        progressDialog.setMessage("Se está agregando su catálogo.");
        progressDialog.setCanceledOnTouchOutside(false);
        titulo = findViewById(R.id.add_nombre_producto);
        descripcion = findViewById(R.id.add_descripcion_producto);
        CardView saveBtn = findViewById(R.id.add_producto_guardarBtn);
        precio = findViewById(R.id.add_precio_producto);
        categoria = getIntent().getStringExtra("categoria");
        Spinner unitSpinner = findViewById(R.id.add_producto_opcion);
        imgMenor = findViewById(R.id.add_producto_imagenMenor);
        imgMayor = findViewById(R.id.add_producto_imagenMayor);
        unidad = findViewById(R.id.add_producto_unidad_medida);
        CardView deleteBtn = findViewById(R.id.add_producto_eliminarBtn);
        if (getIntent().getStringExtra("idProducto") == null) {
            deleteBtn.setVisibility(View.GONE);
            idProducto = UUID.randomUUID().toString();
            seVendio = false;
        } else {
            seVendio = true;
            idProducto = getIntent().getStringExtra("idProducto");
            titulo.setText(getIntent().getStringExtra("titulo"));
            descripcion.setText(getIntent().getStringExtra("descripcion"));
            Picasso.get().load(getIntent().getStringExtra("imgMayor")).into(imgMayor);
            urlMayor = getIntent().getStringExtra("imgMayor");
            seVendio = true;
            categoria = getIntent().getStringExtra("categoria");
            urlMenor = getIntent().getStringExtra("imgMenor");
            Picasso.get().load(getIntent().getStringExtra("imgMenor")).into(imgMenor);
            Toast.makeText(this, getIntent().getStringExtra("unidad"), Toast.LENGTH_SHORT).show();
            precio.setText(getIntent().getStringExtra("precio"));
            deleteBtn.setOnClickListener(v -> new AlertDialog.Builder(AddProducto.this)
                    .setTitle("¿Estás seguro?")
                    .setMessage("Este artículo será eliminado permanentemente de la base de datos.")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> FirebaseDatabase.getInstance().getReference("productos")
                            .child(idProducto).setValue(null).addOnSuccessListener(aVoid -> {
                                startActivity(new Intent(AddProducto.this, MainActivity.class));
                                Toast.makeText(AddProducto.this, "Eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            }))
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show());
        }
        categoria = getIntent().getStringExtra("categoria");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere..");
        progressDialog.setMessage("Estamos añadiendo su catálogo.");
        progressDialog.setCanceledOnTouchOutside(false);
        titulo = findViewById(R.id.add_nombre_producto);
        descripcion = findViewById(R.id.add_descripcion_producto);
        saveBtn = findViewById(R.id.add_producto_guardarBtn);
        precio = findViewById(R.id.add_precio_producto);
        categoria = getIntent().getStringExtra("categoria");
        unitSpinner = findViewById(R.id.add_producto_opcion);
        imgMenor = findViewById(R.id.add_producto_imagenMenor);
        imgMayor = findViewById(R.id.add_producto_imagenMayor);
        unidad = findViewById(R.id.add_producto_unidad_medida);
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, spinnerUnitArr
        );
        unitSpinner.setAdapter(ad);
        unitSpinner.setOnItemSelectedListener(this);
        saveBtn.setOnClickListener(v -> {
            if (!isErrorsInCatalogue()) {
                uploadImageToDb();
            }
        });
    }


    public void addImgMenor(View view) {
        Dexter.withContext(AddProducto.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent i = new Intent();
                        i.setType("image/*");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(i, "Elige la imagen pequeña"), 131);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();

    }

    public void addImgMayor(View view) {
        Dexter.withContext(AddProducto.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent i = new Intent();
                        i.setType("image/*");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(i, "Elige la imagen grande"), 132);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 131 && resultCode == RESULT_OK && data != null) {
            uriMenor = data.getData();
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), uriMenor);
                imgMenor.setImageBitmap(b);
                imgMenorCambia = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (data != null) {
            uriMayor = data.getData();
            try {
                imgMayorCambia = true;
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), uriMayor);
                imgMayor.setImageBitmap(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isErrorsInCatalogue() {
        boolean temp = false;
        if (titulo.getText().toString().isEmpty()) {
            temp = true;
            Toast.makeText(this, "Título inválido", Toast.LENGTH_SHORT).show();
        } else if (descripcion.getText().toString().isEmpty()) {
            temp = true;
            Toast.makeText(this, "Descripción inválida", Toast.LENGTH_SHORT).show();
        } else if (uriMenor == null && !seVendio) {
            Toast.makeText(this, "No se encontró imagen pequeña", Toast.LENGTH_SHORT).show();
            temp = true;
        } else if (uriMayor == null && !seVendio) {
            Toast.makeText(this, "No se encontró imagen grande", Toast.LENGTH_SHORT).show();
            temp = true;
        } else if (precio.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, defina un precio", Toast.LENGTH_SHORT).show();
            temp = true;
        }
        return temp;
    }



    private void saveProductToDb() {
        progressDialog.show();

        modeloProductos product = new modeloProductos(
                categoria,
                urlMayor,
                urlMenor,
                titulo.getText().toString(),
                descripcion.getText().toString(),
                precio.getText().toString(),
                unidad.getText().toString(),
                idProducto);

        FirebaseDatabase.getInstance().getReference("productos").child(idProducto)
                .setValue(product).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            startActivity(new Intent(AddProducto.this, MainActivity.class));
            Toast.makeText(AddProducto.this, "Subido correctamente", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadImageToDb() {
        try {
            progressDialog.show();
            if (seVendio) {
                if (imgMayorCambia && !imgMenorCambia) {
                    FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                            .child("imgMayor").putFile(uriMayor).addOnSuccessListener(taskSnapshot -> FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                            .child("imgMayor").getDownloadUrl().addOnSuccessListener(uri -> {
                                urlMayor = uri.toString();
                                saveProductToDb();
                            }));
                } else if (imgMenorCambia && !imgMayorCambia) {
                    FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                            .child("imgMenor").putFile(uriMenor).addOnSuccessListener(taskSnapshot -> FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                            .child("imgMenor").getDownloadUrl().addOnSuccessListener(uri -> {
                                urlMenor = uri.toString();
                                Toast.makeText(AddProducto.this, "image", Toast.LENGTH_SHORT).show();
                                saveProductToDb();
                            }));
                } else if (imgMenorCambia && imgMayorCambia) {
                    FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                            .child("imgMenor").putFile(uriMenor).addOnSuccessListener(taskSnapshot -> FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                            .child("imgMenor").getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                urlMenor = uri.toString();
                                FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                                        .child("imgMayor").putFile(uriMayor)
                                        .addOnSuccessListener(taskSnapshot1 -> FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                                                .child("imgMayor").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri1) {
                                                        urlMayor = uri1.toString();
                                                        saveProductToDb();
                                                    }
                                                }));
                            }));
                } else {
                    saveProductToDb();
                }
            } else {
                FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                        .child("imgMenor").putFile(uriMenor).addOnSuccessListener(taskSnapshot -> FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                        .child("imgMenor").getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            urlMenor = uri.toString();
                            FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                                    .child("imgMayor").putFile(uriMayor)
                                    .addOnSuccessListener(taskSnapshot12 -> FirebaseStorage.getInstance().getReference("productos").child(idProducto)
                                            .child("imgMayor").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri12) {
                                                    urlMayor = uri12.toString();
                                                    saveProductToDb();
                                                }
                                            }));
                        }));
            }
        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        unidad.setText(spinnerUnitArr[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}